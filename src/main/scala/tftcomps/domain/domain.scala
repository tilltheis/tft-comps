package tftcomps

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

  def search(championPool: Seq[Champion], maxTeamSize: Int): LazyList[Seq[Composition]] = {
    implicit val compositionOrdering: Ordering[Composition] = Ordering.by(_.worth)

    def heuristic(composition: Composition): Long = maxTeamSize * (1 + 12) // change between 4 and 12

    def path(predecessors: Map[Composition, Composition], composition: Composition): Seq[Composition] =
      (composition :: List.unfold(composition)(predecessors.get(_).map(x => (x, x)))).reverse

    def search_(visited: PriorityQueue[Composition],
                predecessors: Map[Composition, Composition],
                currentScores: Map[Composition, Long],
                estimatedScores: Map[Composition, Long]): LazyList[Seq[Composition]] = {
      val (maybeComposition, newVisited) = visited.dequeueMax
      maybeComposition.fold(LazyList.empty[Seq[Composition]]) { composition =>
        if (composition.champions.size == maxTeamSize)
          path(predecessors, composition) #:: search_(newVisited, predecessors, currentScores, estimatedScores)
        else {
          val newArgs =
            championPool.foldLeft((newVisited, predecessors, currentScores, estimatedScores)) {
              case (tmp @ (tmpVisited, tmpPredecessors, tmpCurrentScores, tmpEstimatedScores), champion) =>
                val tmpComposition = composition.add(champion)
                val estimatedScore = currentScores(composition) + Math.abs(composition.worth - tmpComposition.worth)
                if (estimatedScore > currentScores.getOrElse(tmpComposition, 0L)) {
                  (tmpVisited.enqueue(tmpComposition), // could be optimized by batch operation
                   tmpPredecessors + (tmpComposition -> composition),
                   tmpCurrentScores + (tmpComposition -> estimatedScore),
                   tmpEstimatedScores + (tmpComposition -> (estimatedScore + heuristic(tmpComposition))))
                } else tmp
            }
          (search_ _).tupled(newArgs)
        }
      }
    }

    search_(
      visited = PriorityQueue(Composition.empty),
      predecessors = Map.empty,
      currentScores = Map(Composition.empty -> 0),
      estimatedScores = Map(Composition.empty -> heuristic(Composition.empty))
    )
  }

}
