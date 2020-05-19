package tftcomps

package object domain {
  val MaxRolesPerChampion = 3 // Irelia, Gankplank, MissFortune

  case class Role(name: String, stackingBonusThresholds: Set[Int])

  case class Champion(name: String, roles: Set[Role], cost: Int)

  final case class CompositionConfig(maxTeamSize: Int,
                                     maxChampionCost: Int,
                                     requiredRoles: Map[Role, Int],
                                     requiredChampions: Set[Champion],
                                     searchThoroughness: Int)

  final case class Composition(champions: Set[Champion]) {
    val roleCounts: Map[Role, Int] = champions.toSeq
      .flatMap(_.roles.toSeq)
      .groupMapReduce(identity)(_ => 1)(_ + _)

    def size: Int = champions.size

    def add(champion: Champion): Composition = Composition(champions + champion)

    def synergyPercentage: Double = {
      val reachedThresholdRoleCount = roleCounts.collect {
        case (role, count) if role.stackingBonusThresholds.min <= count => count
      }.sum
      reachedThresholdRoleCount.toDouble / roleCounts.values.sum.toDouble
    }
  }

  object Composition {
    val empty: Composition = Composition(Set.empty)
  }

  object MinRoleThresholdSearchBackend {
    def distance(composition: Composition, champion: Champion): Int =
      if (composition.champions.contains(champion)) 0 // shouldn't happen
      else {
        val unreachedThresholdOffset = missingRoleSlotCount(composition.add(champion))
        val fixedStepCost = 1
        fixedStepCost + unreachedThresholdOffset
      }

    def heuristic(composition: Composition, maxTeamSize: Int): Int = {
      val unreachedThresholdOffset = missingRoleSlotCount(composition)
      val distanceToDestination = maxTeamSize - composition.size
      distanceToDestination + unreachedThresholdOffset
    }

    private def missingRoleSlotCount(composition: Composition) =
      composition.roleCounts.collect {
        case (role, count) if role.stackingBonusThresholds.min > count =>
          (role.stackingBonusThresholds.min - count) * 10
      }.sum
  }
  def search(championPool: Seq[Champion],
             maxTeamSize: Int,
             thoroughness: Int,
             requiredRoleCounts: Map[Role, Int] = Map.empty,
             requiredChampions: Set[Champion] = Set.empty): Option[Composition] = {
    searchReturningCost(championPool, maxTeamSize, thoroughness, requiredRoleCounts, requiredChampions).map {
      case composition -> _gScore => composition
    }
  }

  def searchReturningCost(championPool: Seq[Champion],
                          maxTeamSize: Int,
                          thoroughness: Int,
                          requiredRoleCounts: Map[Role, Int] = Map.empty,
                          requiredChampions: Set[Champion] = Set.empty): Option[(Composition, Int)] = {
    val backend = MinRoleThresholdSearchBackend
    val requiredRoles = requiredRoleCounts.filter(_._2 > 0)

    def satisfiesRequirements(composition: Composition): Boolean = {
      val satisfiesRoles = requiredRoles.forall {
        case (requiredRole, requiredCount) =>
          composition.roleCounts.exists {
            case (role, count) => role == requiredRole && count >= requiredCount
          }
      }
      val satisfiesChampions = requiredChampions.subsetOf(composition.champions)

      satisfiesRoles && satisfiesChampions
    }

    def relevantChampionPool(composition: Composition): Seq[Champion] = {
      val championsForRequiredRoles = requiredRoles.flatMap {
        case (role, count) =>
          if (composition.roleCounts.getOrElse(role, 0) < count) championPool.filter(_.roles.contains(role))
          else Seq.empty
      }
      // we cannot use `Set`s here because the champion pool order matters (it influences result order)
      val requiredPool =
        (requiredChampions.toSeq ++ championsForRequiredRoles).distinct.filterNot(composition.champions.contains)
      val globalPool = championPool.filterNot(composition.champions.contains)
      if (requiredPool.nonEmpty) requiredPool else globalPool
    }

    /**
      * @return `LazyList((composition, bound))` so that another algorithm can continue from the result
      */
    def genericIdaStar(root: Composition, maxSize: Int, initialCost: Int = 0, allowIncomplete: Boolean = false)(
        f: (Int, Int) => Int): Option[(Composition, Int)] = {
      val h = backend.heuristic(_, maxTeamSize)
      val d = backend.distance _

      /**
        * @return `Left(newBound)` if not found or `Right((composition, gScore))` if found
        */
      def go(composition: Composition, gScore: Int, bound: Int): Either[Int, (Composition, Int)] = {
        val fScore = f(gScore, h(composition))

        if (fScore > bound) Left(fScore)
        else if (composition.size == maxSize)
          // the requirements might not be satisfiable
          if (allowIncomplete || satisfiesRequirements(composition)) Right(composition -> gScore)
          else Left(Int.MaxValue)
        else
          relevantChampionPool(composition)
            .foldLeft[Either[Int, (Composition, Int)]](Left(Int.MaxValue)) {
              case (x @ Right(_), _) => x
              case (Left(tmpMin), champion) =>
                val result = go(composition.add(champion), gScore + d(composition, champion), bound)
                result.left.map(Math.min(_, tmpMin))
            }
      }

      if (root.size >= maxSize) Some(root -> 0)
      else {
        var bound = h(root)
        var result: Option[(Composition, Int)] = None
        while (result.isEmpty && bound < Int.MaxValue) {
          go(root, initialCost, bound) match {
            case Left(newBound)               => bound = newBound
            case Right(composition -> gScore) => result = Some((composition, gScore))
          }
        }
        result
      }
    }

    def nonGreedyF(g: Int, h: Int): Int = g + h
    def greedyF(g: Int, h: Int): Int = h

    if (requiredChampions.size > maxTeamSize || requiredRoles.size > maxTeamSize * MaxRolesPerChampion) None
    else {
      val initialComposition = Composition.empty
      val firstTeamSize = Math.max(0, maxTeamSize - thoroughness)
      val firstResult = genericIdaStar(
        initialComposition,
        firstTeamSize,
        allowIncomplete = true
      )(greedyF)

      val secondResult = firstResult.flatMap {
        case (secondInitComp, secondInitCost) =>
          val secondTeamSize = maxTeamSize
          genericIdaStar(
            secondInitComp,
            secondTeamSize,
            initialCost = secondInitCost
          )(nonGreedyF)
      }

      secondResult
    }
  }

}
