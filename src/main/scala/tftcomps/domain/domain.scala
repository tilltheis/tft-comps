package tftcomps

import scala.scalajs.js.Math

package object domain {
  case class Role(name: String, stackingBonusThresholds: Set[Int])

  case class Champion(name: String, roles: Set[Role], cost: Int)

  final case class Composition(champions: Set[Champion]) {
    val roles: Map[Role, Int] = champions.toSeq
      .flatMap(_.roles.toSeq)
      .groupMapReduce(identity)(_ => 1)(_ + _)

    val size: Long = champions.size

    val worth: Int =
      if (size == 0) 0
      else
        roles.map {
          case (role, count) =>
            // filter for x > 1 or Mercenary and Starship would be included in the beginning of every comp
            val multiplierOffset = role.stackingBonusThresholds.filter(x => x > 1 && x <= count).maxOption.fold(0) {
              reachedThreshold =>
                val thresholdIndex = role.stackingBonusThresholds.toSeq.sorted.indexOf(reachedThreshold)
                (thresholdIndex + 1) * (12 / role.stackingBonusThresholds.size) // this favors fully stacked roles
//                thresholdIndex + 1 // this favors roles with many thresholds
            }
            count * (1 + multiplierOffset)
        }.sum

    val synergies: Map[Role, Int] = roles
      .map {
        case (role, count) =>
          // filter for x > 1 or Mercenary and Starship would be included in the beginning of every comp
          role -> role.stackingBonusThresholds.filter(x => x > 1 && x <= count).maxOption.getOrElse(0)
      }
      .filter(_._2 > 0)

    def add(champion: Champion): Composition = Composition(champions + champion)

    override def toString: String = s"Composition($champions)[$worth]"
  }
  object Composition {
    val empty: Composition = Composition(Set.empty)
  }

  class PriorityQueue[A: Ordering] private (unsortedList: List[A]) {
    private val sortedList: List[A] = unsortedList.sorted(implicitly[Ordering[A]].reverse)
    def enqueue(x: A): PriorityQueue[A] = new PriorityQueue(x :: sortedList)
    def dequeueMax: (Option[A], PriorityQueue[A]) =
      if (sortedList.isEmpty) (None, this) else (sortedList.headOption, new PriorityQueue(sortedList.tail))

    override def toString: String = "PriorityQueue(" + sortedList.mkString(",") + ")"
  }
  object PriorityQueue {
    def apply[A: Ordering](xs: A*): PriorityQueue[A] = new PriorityQueue[A](List(xs: _*))
    def empty[A: Ordering]: PriorityQueue[A] = new PriorityQueue[A](Nil)
  }

  def search(championPool: Seq[Champion],
             maxTeamSize: Int,
             requiredRoles: Set[Role] = Set.empty,
             requiredChampions: Set[Champion] = Set.empty): LazyList[Composition] = {
    implicit val compositionOrdering: Ordering[Composition] = Ordering.by(_.worth)

    def heuristic(composition: Composition): Long =
      maxTeamSize * (1 + 12) - composition.worth // change between 4 and 12

    def satisfiesRequirements(composition: Composition): Boolean = {
      val satisfiesRoles = requiredRoles.forall(requiredRole =>
        composition.roles.exists {
          case (`requiredRole`, count) if count >= requiredRole.stackingBonusThresholds.min => true
          case _                                                                            => false
      })
      val satisfiesChampions = requiredChampions.forall(composition.champions.contains)

      satisfiesRoles && satisfiesChampions
    }

    def satisfiesRequirementsOrCompTooSmall(composition: Composition): Boolean = {
      val championsLackingRequiredRoles = composition.champions.filterNot(_.roles.exists(requiredRoles.contains))
      satisfiesRequirements(composition) || championsLackingRequiredRoles.isEmpty || championsLackingRequiredRoles
        .forall(requiredChampions.contains)
    }

    def search_(visited: PriorityQueue[Composition],
                currentScores: Map[Composition, Long],
                estimatedScores: Map[Composition, Long]): LazyList[Composition] = {
      val (maybeComposition, newVisited) = visited.dequeueMax
      maybeComposition.fold(LazyList.empty[Composition]) { composition =>
        if (composition.champions.size == maxTeamSize)
          if (satisfiesRequirements(composition))
            composition #:: search_(newVisited, currentScores, estimatedScores)
          else search_(newVisited, currentScores, estimatedScores)
        else {
          val newArgs =
            championPool.foldLeft((newVisited, currentScores, estimatedScores)) {
              case (tmp @ (tmpVisited, tmpCurrentScores, tmpEstimatedScores), champion) =>
                val tmpComposition = composition.add(champion)
                val estimatedScore = currentScores(composition) + Math.abs(composition.worth - tmpComposition.worth)
                if (estimatedScore > currentScores.getOrElse(tmpComposition, 0L) && satisfiesRequirementsOrCompTooSmall(
                      tmpComposition)) {
                  (tmpVisited.enqueue(tmpComposition), // could be optimized by batch operation
                   tmpCurrentScores + (tmpComposition -> estimatedScore),
                   tmpEstimatedScores + (tmpComposition -> (estimatedScore + heuristic(tmpComposition))))
                } else tmp
            }
          (search_ _).tupled(newArgs)
        }
      }
    }

    // champions have 3 roles max
    if (requiredChampions.size > maxTeamSize || requiredRoles.size > maxTeamSize * 3) LazyList.empty
    else {
      val initialComposition = Composition(requiredChampions)
      search_(
        visited = PriorityQueue(initialComposition),
        currentScores = Map(initialComposition -> 0),
        estimatedScores = Map(initialComposition -> heuristic(initialComposition))
      )
    }
  }

}
