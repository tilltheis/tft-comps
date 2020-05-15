package tftcomps

package object domain {
  val MinRolesPerChampion = 2
  val MaxRolesPerChampion = 3 // Irelia, Gankplank, MissFortune
  val MaxThreshold = 9 // DarkStar
  val MaxThresholdIndex = 3 // Sorcerer

  case class Role(name: String, stackingBonusThresholds: Set[Int])

  case class Champion(name: String, roles: Set[Role], cost: Int)

  trait Scorer extends (Composition => Double)

  // favors fully stacked roles with many thresholds (absolute role progress)
  object AbsoluteThresholdsAndFewRolesScorer extends Scorer {
    def maxScore(compositionSize: Int): Int =
      MaxThreshold * compositionSize + compositionSize + (MaxRolesPerChampion - compositionSize)

    def score(composition: Composition): Int = {
      val absoluteThresholdScore = composition.reachedThresholds.values.sum
      val sizeScore = composition.size.toInt
      val complexityScore = MaxRolesPerChampion * composition.size.toInt - composition.roles.size.toInt // boost simple comps w/ few roles
      absoluteThresholdScore + sizeScore + complexityScore
    }

    override def apply(composition: Composition): Double =
      score(composition).toDouble / maxScore(composition.size.toInt)
  }

  // favors fully stacked roles with few thresholds (relative role progress)
  object RelativeThresholdScorer extends Scorer {
    def maxScore(compositionSize: Int): Int =
      (MaxThresholdIndex + 1) * 12 * compositionSize + compositionSize

    def score(composition: Composition): Int = {
      val relativeThresholdScore = composition.reachedThresholdIndexes.map {
        case (role, index) =>
          // 12 is divisable by all threshold indexes + 1 (ie 1, 2, 3)
          (index + 1) * (12 / role.stackingBonusThresholds.size)
      }.sum
      val sizeScore = composition.size.toInt
      relativeThresholdScore + sizeScore
    }

    override def apply(composition: Composition): Double =
      score(composition).toDouble / maxScore(composition.size.toInt)
  }

  // values both absolute stack levels and relative stack levels
  object BalancedThresholdScorer extends Scorer {
    def maxScore(compositionSize: Int): Int =
      MaxThreshold * 3 * compositionSize + (MaxThresholdIndex + 1) * 12 * compositionSize + compositionSize

    def score(composition: Composition): Int = {
      // use 4 because 4 * 3 is 12 where 3 is max threshold index (max absolute threshold should yield same value as max relative threshold)
      val absoluteThresholdScore = 3 * composition.reachedThresholdIndexes.values.sum // maybe math.min? // before it was 4 because it capped thresholds at 3 to compensate sorcerers
      // before:      4 * (Math.min(2, thresholdIndex) + 1) + (thresholdIndex + 1) * (12 / role.stackingBonusThresholds.size) // big synergy groups are great but fully stacked ones as well (max 2 to counter sorcerer w/ 4 thresholds) (add 50% or 100% max points for full stacks)
      val relativeThresholdScore = composition.reachedThresholdIndexes.map {
        case (role, index) =>
          // 12 is divisable by all threshold indexes + 1 (ie 1, 2, 3)
          (index + 1) * (12 / role.stackingBonusThresholds.size)
      }.sum
      val sizeScore = composition.size.toInt
      absoluteThresholdScore + relativeThresholdScore + sizeScore
    }

    override def apply(composition: Composition): Double =
      score(composition).toDouble / maxScore(composition.size.toInt)
  }

  // todo: remove heuristic because it's pointless in this case because our scores never shrink

  final case class Composition(champions: Set[Champion]) {
    val roles: Map[Role, Int] = champions.toSeq
      .flatMap(_.roles.toSeq)
      .groupMapReduce(identity)(_ => 1)(_ + _)

    @inline def size: Int = champions.size

    def roleMembers(role: Role): Set[Champion] = champions.filter(_.roles.contains(role))

    // 0 means not reached
    def reachedThresholds: Map[Role, Int] = roles.map {
      // filter for x > 1 or Mercenary and Starship would be included in the beginning of every comp
      case (role, count) => role -> role.stackingBonusThresholds.filter(x => x > 1 && x <= count).maxOption.getOrElse(0)
    }

    // -1 means not reached
    def reachedThresholdIndexes: Map[Role, Int] = reachedThresholds.map {
      case (role, threshold) => role -> role.stackingBonusThresholds.toSeq.sorted.indexOf(threshold)
    }

    def score: Int =
      if (size == 0) 0
      else
        roles.map {
          case (role, count) =>
            // filter for x > 1 or Mercenary and Starship would be included in the beginning of every comp
//            val multiplier = role.stackingBonusThresholds.filter(x => x > 1 && x <= count).maxOption.fold(0) {
            val multiplier = role.stackingBonusThresholds.filter(x => x > 1 && x <= count).maxOption.fold(0) {
              reachedThreshold =>
                val thresholdIndex = role.stackingBonusThresholds.toSeq.sorted.indexOf(reachedThreshold)
                (thresholdIndex + 1) * (12 / role.stackingBonusThresholds.size) // this favors fully stacked roles
                1 + thresholdIndex + 1 // this favors roles with many thresholds
                4 * (Math.min(2, thresholdIndex) + 1) + (thresholdIndex + 1) * (12 / role.stackingBonusThresholds.size) // big synergy groups are great but fully stacked ones as well (max 2 to counter sorcerer w/ 4 thresholds) (add 50% or 100% max points for full stacks)
                (thresholdIndex + 1) * 12
//                12 // simply count if threshold is reached or not
//                1
                reachedThreshold
            }
            multiplier
//            count * multiplier
        }.sum + size.toInt + (3 * size.toInt - roles.size.toInt) // boost simple comps w/ few roles

    def synergies: Map[Role, Int] =
      roles
        .map {
          case (role, count) =>
            // filter for x > 1 or Mercenary and Starship would be included in the beginning of every comp
            role -> role.stackingBonusThresholds.filter(x => x > 1 && x <= count).maxOption.getOrElse(0)
        }
        .filter(_._2 > 0)

    def add(champion: Champion): Composition = Composition(champions + champion)

    override def toString: String = s"Composition($champions)[$score]"
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

  locally {
    val role1 = Role("role1", Set(1, 2, 8))
    val role2 = Role("role2", Set(1, 2, 8))
    val role3 = Role("role3", Set(1, 2, 8))
    val champs = (0 until 8).map(i => Champion(s"champ$i", Set(role1, role2, role3), 5))
    val perfectComp = Composition(champs.toSet)
    println("perfect comp score = " + perfectComp.score)
  }

  trait SearchBackend {
    def distance(composition: Composition, champion: Champion): Int
    def heuristic(composition: Composition, maxTeamSize: Int): Int
    def qualityPercentage(composition: Composition): Double
  }

  object AnyRoleThresholdSearchBackend extends SearchBackend {
    // todo: don't count single threshold roles as imperfection
    def qualityPercentage(composition: Composition): Double =
      1.0d - missingRoleSlotCount(composition).toDouble / composition.champions.toSeq.map(_.roles.size).sum

    // distance is at least one - minimum possible distance for team would be team size
    override def distance(composition: Composition, champion: Champion): Int =
      if (composition.champions.contains(champion)) 0 // shouldn't happen
      else {
        val unreachedThresholdCount = missingRoleSlotCount(composition.add(champion))
        1 + unreachedThresholdCount * 10
      }

    // if admissible (doesn't overestimate) then the best shortest path will be found but that can be slow
    override def heuristic(composition: Composition, maxTeamSize: Int): Int = {
      val unreachedThresholdCount = missingRoleSlotCount(composition)
      val distanceToDestination = maxTeamSize - composition.size
      distanceToDestination + unreachedThresholdCount * 10
    }

    private def missingRoleSlotCount(composition: Composition) =
      composition.reachedThresholds.collect {
        case (role, 0) => composition.roleMembers(role).size
      }.sum
  }

  object AbsoluteRoleThresholdsSearchBackend extends SearchBackend {
    def qualityPercentage(composition: Composition): Double = 0.0d

    // distance is at least one - minimum possible distance for team would be team size
    override def distance(composition: Composition, champion: Champion): Int =
      if (composition.champions.contains(champion)) 0 // shouldn't happen
      else {
        val thresholdCountBefore = composition.reachedThresholds.values.sum
        val thresholdCountAfter = composition.add(champion).reachedThresholds.values.sum
        MaxRolesPerChampion + thresholdCountAfter - thresholdCountBefore + 1
      }

    override def heuristic(composition: Composition, maxTeamSize: Int): Int = {
      val currentThresholdCount = composition.reachedThresholds.values.sum
      val maxThresholdCount = composition.roles.keySet.map(_.stackingBonusThresholds.max).sum
      val thresholdScore = (maxThresholdCount - currentThresholdCount) / MaxRolesPerChampion
      val sizeScore = maxTeamSize - composition.size
      thresholdScore + sizeScore
    }
  }

  def search(championPool: Seq[Champion],
             maxTeamSize: Int,
             requiredRoles: Set[Role] = Set.empty,
             requiredChampions: Set[Champion] = Set.empty): LazyList[Composition] =
    search2(championPool, maxTeamSize, requiredRoles, requiredChampions).map(_._1)

  def search2(championPool: Seq[Champion],
              maxTeamSize: Int,
              requiredRoles: Set[Role] = Set.empty,
              requiredChampions: Set[Champion] = Set.empty): LazyList[(Composition, Double)] = {
    val backend: SearchBackend = AnyRoleThresholdSearchBackend
//    val backend: SearchBackend = AbsoluteRoleThresholdsSearchBackend

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

    // idea: always initialize with role (loop), best first search for teamsize-2 and a* for last 1 or 2 levels
    // idea: seed search with 1 or 2 role (maybe using Set#subsets for variation, eg (0 until 9).toSet.subsets(3).size * 23 = 1932)

    /**
      * This impl is super slow but follows the textbook A* code.
      *
      * @param fScore (g, h) => f
      *               - use only g => uniform-cost search
      *               - use only h => greedy best-first search
      *               - use both g and h => A* search
      * @return pairs of composition and their fScore
      */
    def naiveGenericSearch(fScore: (Int, Int) => Int)(openSet: List[Composition],
                                                      g: Map[Composition, Int],
                                                      f: Map[Composition, Int],
                                                      limit: Int): LazyList[(Composition, Int)] = {
      val h = backend.heuristic(_, limit)
      val d = backend.distance _
      val sortedVisited = openSet.sortBy(x => fScore(g(x), h(x)))
      sortedVisited match {
        case Nil => LazyList.empty[(Composition, Int)]
        case composition :: newOpenSet =>
          if (composition.size == limit)
            if (satisfiesRequirements(composition))
              (composition, g(composition)) #:: naiveGenericSearch(fScore)(newOpenSet, g, f, limit)
            else naiveGenericSearch(fScore)(newOpenSet, g, f, limit)
          else {
            val newArgs = championPool.foldLeft((newOpenSet, g, f, limit)) {
              case (tmp @ (tmpVisited, tmpG, tmpF, tmpLimit), champion) =>
                val tmpComposition = composition.add(champion)
                val tmpGScore = tmpG(composition) + d(composition, champion)
                if (tmpGScore < tmpG.getOrElse(tmpComposition, Int.MaxValue) && satisfiesRequirementsOrCompTooSmall(
                      tmpComposition)) {
                  (tmpComposition :: tmpVisited,
                   tmpG + (tmpComposition -> tmpGScore),
                   tmpF + (tmpComposition -> (tmpGScore + h(tmpComposition))),
                   tmpLimit)
                } else tmp
            }
            (naiveGenericSearch(fScore) _).tupled(newArgs)
          }
      }
    }

    /**
      * This impl is quite fast because it follows the IDA* code.
      */
    def genericSearch(f: (Int, Int) => Int)(root: Composition,
                                            maxSize: Int,
                                            initialCost: Int = 0,
                                            allowIncomplete: Boolean = false): LazyList[(Composition, Int)] = {
      val h = backend.heuristic(_, maxSize)
      val d = backend.distance _

      /**
        * @return `Left(new bound)` if not found or `Right((composition, bound))` if found
        */
      def go(composition: Composition, gScore: Int, bound: Int): Either[Int, (Composition, Int)] = {
        val fScore = f(gScore, h(composition))

        if (!satisfiesRequirementsOrCompTooSmall(composition)) Left(Int.MaxValue)
        else if (fScore > bound) Left(fScore)
        else if (composition.size == maxSize) // todo: think about only allowing perfect synergies
          // if this produces an unfortunate incomplete result, the 2nd algo might not be able to find anything to complete the team
          if ((allowIncomplete && satisfiesRequirementsOrCompTooSmall(composition)) ||
              satisfiesRequirements(composition)) Right(composition -> fScore)
          else Left(Int.MaxValue)
        else
          championPool
            .filterNot(composition.champions.contains)
            .foldLeft[Either[Int, (Composition, Int)]](Left(Int.MaxValue)) {
              case (x @ Right(_), _) => x
              case (Left(tmpMin), champion) =>
                val result = go(composition.add(champion), gScore + d(composition, champion), bound)
                result.left.map(Math.min(_, tmpMin))
            }
      }

      val results =
        if (root.size >= maxSize) LazyList(Some(root -> 0))
        else
          LazyList.unfold(Option(h(root))) { maybeBound =>
            maybeBound.fold[Option[(Option[(Composition, Int)], Option[Int])]](None) { bound =>
              go(root, initialCost, bound) match {
                case Left(Int.MaxValue)             => None
                case Left(newBound)                 => Some((None, Some(newBound)))
                case Right(composition -> newBound) => Some((Some((composition, newBound)), None))
              }
            }
          }

      results.collect {
        case Some(x) => x
      }
    }

    val nonGreedySearch = genericSearch((g, h) => g + h) _
    val greedySearch = genericSearch((g, h) => h) _

    if (requiredChampions.size > maxTeamSize || requiredRoles.size > maxTeamSize * MaxRolesPerChampion) LazyList.empty
    else {
      val initialComposition = Composition(requiredChampions)
      val firstTeamSize = Math.max(0, maxTeamSize - 2)
      val firstResult: LazyList[(Composition, Int)] = greedySearch(
        initialComposition,
        firstTeamSize,
        0, // initialCost
        true // allowIncomplete
      ).take(1) // todo dont forget

      val secondResult = firstResult.headOption.fold(LazyList.empty[(Composition, Int)]) {
        case (secondInitComp, secondInitCost) =>
          val secondTeamSize = maxTeamSize
          nonGreedySearch(
            secondInitComp,
            secondTeamSize,
            secondInitCost, // initialCost
            false // allowIncomplete
          ).map { case (composition, finalCost) => composition -> (finalCost) }
      }

      secondResult.map {
        case composition -> _bound => composition -> backend.qualityPercentage(composition)
      }
    }

//    val aStarSearch = naiveGenericSearch((g, h) => g + h) _
//    val greedySearch = naiveGenericSearch((g, h) => h) _
//
//    if (requiredChampions.size > maxTeamSize || requiredRoles.size > maxTeamSize * MaxRolesPerChampion) LazyList.empty
//    else {
//      val initialComposition = Composition(requiredChampions)
//
//      val firstTeamSize = Math.max(0, maxTeamSize - 2)
//      val firstResult: LazyList[(Composition, Int)] = greedySearch(
//        List(initialComposition),
//        Map(initialComposition -> 0),
//        Map(initialComposition -> backend.heuristic(initialComposition, maxTeamSize)),
//        firstTeamSize
//      ).take(1)
//
//      val secondTeamSize = maxTeamSize
//      val secondResult = aStarSearch(
//        firstResult.map(_._1).toList,
//        firstResult.toMap,
//        firstResult.map {
//          case (composition, _) => composition -> backend.heuristic(composition, maxTeamSize)
//        }.toMap,
//        secondTeamSize
//      )
//
//      secondResult
//    }

//    if (requiredChampions.size > maxTeamSize || requiredRoles.size > maxTeamSize * MaxRolesPerChampion) LazyList.empty
//    else {
//      val initialComposition = Composition(requiredChampions)
//      greedySearch(
//        List(initialComposition),
//        Map(initialComposition -> 0),
//        Map(initialComposition -> backend.heuristic(initialComposition, maxTeamSize))
//      )
//    }
  }

}
