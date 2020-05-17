package tftcomps

package object domain {
  val MinRolesPerChampion = 2
  val MaxRolesPerChampion = 3 // Irelia, Gankplank, MissFortune
  val MaxThreshold = 9 // DarkStar
  val MaxThresholdIndex = 3 // Sorcerer

  case class Role(name: String, stackingBonusThresholds: Set[Int]) {
    val sortedThresholdsArray: Array[Int] = stackingBonusThresholds.toSeq.sorted.toArray
  }

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

    def reachedThresholdIndexes: Map[Role, Int] = reachedThresholds.map {
      case (role, threshold) => role -> role.stackingBonusThresholds.toSeq.sorted.indexOf(threshold)
    }

    // -1 means not reached
    @inline def reachedThresholdIndex(role: Role, count: Int): Int = {
      //      // this will be called often
      var inLoop = true
      var i = role.sortedThresholdsArray.length - 1
      var reachedIndex = -1

      while (inLoop) {
        if (i == -1 || role.sortedThresholdsArray(i) <= count) {
          reachedIndex = i
          inLoop = false
        }
        i -= 1
      }

      reachedIndex
      val x = role.sortedThresholdsArray.indexWhere(_ <= count)
      println(s"manual=$reachedIndex auto=$x count=$count for ${role.sortedThresholdsArray.mkString(",")}")
      x
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

  object MinRoleThresholdSearchBackend extends SearchBackend {
    // todo: don't count single threshold roles as imperfection
    def qualityPercentage(composition: Composition): Double =
      1.0d - missingRoleSlotCount(composition).toDouble / composition.roles.values.sum

    // distance is at least one - minimum possible distance for team would be team size
    override def distance(composition: Composition, champion: Champion): Int =
      if (composition.champions.contains(champion)) 0 // shouldn't happen
      else {
        val unreachedThresholdCount = missingRoleSlotCount(composition.add(champion))
        1 + unreachedThresholdCount
      }

    // if admissible (doesn't overestimate) then the best shortest path will be found but that can be slow
    override def heuristic(composition: Composition, maxTeamSize: Int): Int = {
      val unreachedThresholdCount = missingRoleSlotCount(composition)
      val distanceToDestination = maxTeamSize - composition.size
      distanceToDestination + unreachedThresholdCount
    }

    private def missingRoleSlotCount(composition: Composition) =
      composition.roles.collect {
//        case (role, count) if count < role.stackingBonusThresholds.min => count * 10
        case (role, count) if count < role.stackingBonusThresholds.min =>
          (role.stackingBonusThresholds.min - count) * 10
      }.sum
  }

  object MaxRoleThresholdsSearchBackend extends SearchBackend {
    // todo: don't count single threshold roles as imperfection
    def qualityPercentage(composition: Composition): Double =
      1.0d - missingRoleSlotCount(composition).toDouble / composition.roles.values.sum

    // distance is at least one - minimum possible distance for team would be team size
    override def distance(composition: Composition, champion: Champion): Int =
      if (composition.champions.contains(champion)) 0 // shouldn't happen
      else {
        val unreachedThresholdCount = missingRoleSlotCount(composition.add(champion))
        1 + unreachedThresholdCount
      }

    // if admissible (doesn't overestimate) then the best shortest path will be found but that can be slow
    override def heuristic(composition: Composition, maxTeamSize: Int): Int = {
      val unreachedThresholdCount = missingRoleSlotCount(composition)
      val distanceToDestination = maxTeamSize - composition.size
      distanceToDestination + unreachedThresholdCount
    }

    private def missingRoleSlotCount(composition: Composition) =
      composition.roles.collect {
        // this favors roles w/ low thresholds
        // there is also no progress indicator. stacking the role will actually yield a worse score until it's full
        //        case (role, count) if count < role.stackingBonusThresholds.max => count

        //this favors roles w/ low thresholds
        //        case (role, count) => role.stackingBonusThresholds.max - count

        // this kinda works but is slow and still favors roles w/ low thresholds
        //        case (role, count) =>
        //          (12 - indexPlus1 * (12 / role.stackingBonusThresholds.size)) -
        //            (count - composition.reachedThresholds(role))

        // this is good but ignores thresholds (produces eg 5 sorcs instead of the 6 threshold)
        //case (role, count) => MaxThreshold - count

        // this kinda works but is super slow and only really finds sorcs
        //        case (role, count) =>
        //          (MaxThreshold - count) * (role.stackingBonusThresholds.size - composition.reachedThresholdIndex(role, count))

        case (role, count) =>
          if (role.sortedThresholdsArray.length == 1) 0
          else
            role.sortedThresholdsArray.zip(role.sortedThresholdsArray.tail).zipWithIndex.foldLeft(0) {
              case (sum, ((thresholdFrom, thresholdTo), index)) =>
                if (count >= thresholdTo) sum
                else if (count >= thresholdFrom && count < thresholdTo) sum + (thresholdTo - count) * index
                else sum + (thresholdTo - thresholdFrom) * index
            }
      }.sum
  }

  object RelativeMaxRoleThresholdsSearchBackend extends SearchBackend {
    def qualityPercentage(composition: Composition): Double =
      1.0d - missingRoleSlotCount(composition).toDouble / composition.roles.values.sum

    // distance is at least one - minimum possible distance for team would be team size
    override def distance(composition: Composition, champion: Champion): Int =
      if (composition.champions.contains(champion)) 0 // shouldn't happen
      else {
        val unreachedThresholdCount = missingRoleSlotCount(composition.add(champion))
        (1 + unreachedThresholdCount)
      }

    // if admissible (doesn't overestimate) then the best shortest path will be found but that can be slow
    override def heuristic(composition: Composition, maxTeamSize: Int): Int = {
      val unreachedThresholdCount = missingRoleSlotCount(composition)
      val distanceToDestination = maxTeamSize - composition.size
      (distanceToDestination + unreachedThresholdCount)
    }

    private def missingRoleSlotCount__(composition: Composition): Int = {
      composition.roles.map {
        case (role, count) =>
          var inLoop = true
          var i = role.sortedThresholdsArray.length - 1
          var reachedIndex = 0
          while (inLoop) {
            //            if (i == role.sortedThresholdsArray.length || role.sortedThresholdsArray(i) > count) { // original
            if (role.sortedThresholdsArray(i) <= count) {
              reachedIndex = i
              inLoop = false
            }
            i -= 1
          }
          (12 - (reachedIndex + 1) * (12 / role.stackingBonusThresholds.size)) +
            (if (count < role.stackingBonusThresholds.min) count * 10 else 0)
        //          if (count < role.stackingBonusThresholds.min) count * 10
        //          else 12 - (reachedIndex + 1) * (12 / role.stackingBonusThresholds.size)
      }.sum
    }

    // from backup
    private def missingRoleSlotCount_(composition: Composition): Int = {
      composition.roles.map {
        case (role, count) =>
          var inLoop = true
          var i = 0
          var reachedIndex = 0
          while (inLoop) {
            if (i == role.sortedThresholdsArray.length || role.sortedThresholdsArray(i) > count) {
              reachedIndex = i - 1
              inLoop = false
            }
            i += 1
          }
          println(s"$reachedIndex for ($role, $count) in $composition")
          (12 - (reachedIndex + 1) * (12 / role.stackingBonusThresholds.size)) +
            (if (count < role.stackingBonusThresholds.min) count * 10 else 0)
        //          if (count < role.stackingBonusThresholds.min) count * 10
        //          else 12 - (reachedIndex + 1) * (12 / role.stackingBonusThresholds.size)
      }.sum
    }

    // this is the good one!
    private def missingRoleSlotCount(composition: Composition): Int =
      composition.roles.map {
        case (role, count) =>
          // this counts diff to max stack for each role
          // the old scorer simply gave a boost for a big stack
          // how can we reproduce that?
          count * (12 - (composition.reachedThresholdIndex(role, count) + 1) * (12 / role.stackingBonusThresholds.size)) +
            //          count * (role.sortedThresholdsArray.last - count) +
            (if (count < role.stackingBonusThresholds.min) count * 120 else 0)
      }.sum
  }

  // idea: require users to specify the role level. then we can pre-fill the search with champions and do a MinRoleThreshold
  // search from there.
  def search(championPool: Seq[Champion],
             maxTeamSize: Int,
             requiredRoles: Set[Role] = Set.empty,
             requiredChampions: Set[Champion] = Set.empty): LazyList[Composition] =
    search2(championPool,
            maxTeamSize,
            requiredRoles.map(r => r -> r.stackingBonusThresholds.min).toMap,
            requiredChampions).map(_._1)

  def searchWithDetailedRoles(championPool: Seq[Champion],
                              maxTeamSize: Int,
                              requiredRoles: Map[Role, Int] = Map.empty,
                              requiredChampions: Set[Champion] = Set.empty): LazyList[Composition] =
    search2(championPool, maxTeamSize, requiredRoles, requiredChampions).map(_._1)

  def search2(championPool: Seq[Champion],
              maxTeamSize: Int,
              requiredRoleCounts: Map[Role, Int] = Map.empty,
              requiredChampions: Set[Champion] = Set.empty): LazyList[(Composition, Double)] = {
    val backend: SearchBackend = MinRoleThresholdSearchBackend
    //    val backend: SearchBackend = MaxRoleThresholdsSearchBackend
    //    val backend: SearchBackend = RelativeMaxRoleThresholdsSearchBackend

    val requiredRoles = requiredRoleCounts.filter(_._2 > 0)

    def satisfiesRequirements(composition: Composition): Boolean = {
      val satisfiesRoles = requiredRoles.forall {
        case (requiredRole, requiredCount) =>
          composition.roles.exists {
            case (`requiredRole`, count) if count >= requiredCount => true
            case _                                                 => false
          }
      }
      val satisfiesChampions = requiredChampions.forall(composition.champions.contains)

      satisfiesRoles && satisfiesChampions
    }

    def satisfiesRequirementsOrCompTooSmall(composition: Composition): Boolean = {
      val championsLackingRequiredRoles = composition.champions.filterNot(_.roles.exists(requiredRoles.contains))
      satisfiesRequirements(composition) || championsLackingRequiredRoles.isEmpty ||
      championsLackingRequiredRoles.forall(requiredChampions.contains)
    }

    // idea: always initialize with role (loop), best first search for teamsize-2 and a* for last 1 or 2 levels
    // idea: seed search with 1 or 2 role (maybe using Set#subsets for variation, eg (0 until 9).toSet.subsets(3).size * 23 = 1932)

    /**
      * This impl is quite fast because it follows the IDA* code.
      */
    def genericIdaStar(f: (Int, Int) => Int)(root: Composition,
                                             maxSize: Int,
                                             initialCost: Int = 0,
                                             allowIncomplete: Boolean = false): LazyList[(Composition, Int)] = {
      val h = backend.heuristic(_, maxSize)
      val d = backend.distance _

      /**
        * @return `Left(new bound)` if not found or `Right((composition, gScore))` if found
        */
      def go(composition: Composition, gScore: Int, bound: Int): Either[Int, (Composition, Int)] = {
        val fScore = f(gScore, h(composition))

        if (!satisfiesRequirementsOrCompTooSmall(composition)) Left(Int.MaxValue)
        else if (fScore > bound) Left(fScore)
        else if (composition.size == maxSize) // todo: think about only allowing perfect synergies
          // if this produces an unfortunate incomplete result, the 2nd algo might not be able to find anything to complete the team
          if ((allowIncomplete && satisfiesRequirementsOrCompTooSmall(composition)) ||
              satisfiesRequirements(composition)) Right(composition -> gScore)
          else Left(Int.MaxValue)
        else
          championPool
          // todo: extend this to only provide values that satisfy requirements
          // instead of having `allowIncomplete` and `satisfiesRequirements` and friends
            .filterNot(composition.champions.contains)
            .foldLeft[Either[Int, (Composition, Int)]](Left(Int.MaxValue)) {
              case (x @ Right(_), _) => x
              case (Left(tmpMin), champion) =>
                val result = go(composition.add(champion), gScore + d(composition, champion), bound)
                result.left.map(Math.min(_, tmpMin))
            }
      }

      if (root.size >= maxSize) LazyList(root -> 0)
      else
        LazyList
          .unfold(Option(h(root))) { maybeBound =>
            maybeBound.flatMap { bound =>
              go(root, initialCost, bound) match {
                case Left(Int.MaxValue)             => None
                case Left(newBound)                 => Some((None, Some(newBound)))
                case Right(composition -> newBound) => Some((Some((composition, newBound)), None))
              }
            }
          }
          .collect {
            case Some(x) => x
          }
    }

    val nonGreedySearch = genericIdaStar((g, h) => g + h) _
    val greedySearch = genericIdaStar((g, h) => h) _

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
  }

}
