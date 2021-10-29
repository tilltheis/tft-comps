package tftcomps.domain

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import scala.util.Random

class Test extends AnyWordSpec with Matchers with TypeCheckedTripleEquals with ScalaCheckDrivenPropertyChecks {
  "Composition" should {
    "calculate a correct synergyPercentage" in {
      val role1 = Role("role1", Set(2))
      val role2 = Role("role2", Set(3))
      val role3 = Role("role3", Set(2))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role1, role2, role3), 1)
      val champ3 = Champion("champ3", Set(role2), 1)

      Composition(Set(champ1, champ2, champ3)).synergyPercentage should ===((2.0 / 5.0) +- 0.1d)
    }
  }

  "search" should {
    def searchWithMinRoleThresholds(championPool: Seq[Champion],
                                    maxTeamSize: Int,
                                    requiredRoles: Set[Role] = Set.empty,
                                    requiredChampions: Set[Champion] = Set.empty): Option[Composition] =
      search(championPool,
             maxTeamSize,
             2,
             requiredRoles.map(r => r -> r.stackingBonusThresholds.min).toMap,
             requiredChampions)

    "find the best composition (more or less)" in {
      val role1 = Role("role1", Set(2))
      val role2 = Role("role2", Set(2))
      val role3 = Role("role3", Set(3))
      val role4 = Role("role4", Set(1))
      val champ1 = Champion("champ1", Set(role1, role2), 1)
      val champ2 = Champion("champ2", Set(role1), 1)
      val champ3 = Champion("champ3", Set(role3), 1)
      val champ4 = Champion("champ4", Set(role2, role4), 1)
      val allChampions = Seq(champ1, champ2, champ3, champ4)

      searchWithMinRoleThresholds(allChampions, 2).get should {
        equal(Composition(Set(champ1, champ2))) or
          equal(Composition(Set(champ1, champ4)))
      }

      searchWithMinRoleThresholds(allChampions, 3).get should ===(Composition(Set(champ1, champ2, champ4)))
    }

    "be possible around a required set of champions" in {
      val role1 = Role("role1", Set(1))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role1), 1)
      val champ3 = Champion("champ3", Set(role1), 1)
      val champ4 = Champion("champ4", Set(role1), 1)
      val allChampions = Seq(champ1, champ2, champ3, champ4)

      val result = searchWithMinRoleThresholds(allChampions, 3, requiredChampions = Set(champ1, champ2))
      result should not be empty
      result.get should {
        equal(Composition(Set(champ1, champ2, champ3))) or
          equal(Composition(Set(champ1, champ2, champ4)))
      }
    }

    "find nothing when the number of required champions is greater than the team size" in {
      val role1 = Role("role1", Set(1))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role1), 1)
      val champ3 = Champion("champ3", Set(role1), 1)
      val champ4 = Champion("champ4", Set(role1), 1)
      val allChampions = Seq(champ1, champ2, champ3, champ4)

      searchWithMinRoleThresholds(allChampions, 1, requiredChampions = Set(champ1, champ2)) shouldBe empty
    }

    "be possible around a required set of roles" in {
      val role1 = Role("role1", Set(1))
      val role2 = Role("role2", Set(1))
      val role3 = Role("role3", Set(1))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role2), 1)
      val champ3 = Champion("champ3", Set(role3), 1)
      val champ4 = Champion("champ4", Set(role2), 1)
      val champ5 = Champion("champ5", Set(role2), 1)
      val champ6 = Champion("champ6", Set(role3), 1)
      val allChampions = Seq(champ1, champ2, champ3, champ4, champ5, champ6)

      val result1 = searchWithMinRoleThresholds(allChampions, 3, requiredRoles = Set(role1, role2))
      result1 should not be empty
      result1.get.roleCounts.keySet should contain allElementsOf Set(role1, role2)

      val result2 = searchWithMinRoleThresholds(allChampions, 2, requiredRoles = Set(role3))
      result2 should not be empty
      result2.get.roleCounts.keySet should contain allElementsOf Set(role3)
    }

    "be possible around a required set of roles, including zeros" in {
      val role1 = Role("role1", Set(1))
      val role2 = Role("role2", Set(1))
      val role3 = Role("role3", Set(1))
      val allRoles = Set(role1, role2, role3)
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role2), 1)
      val champ3 = Champion("champ3", Set(role3), 1)
      val champ4 = Champion("champ4", Set(role2), 1)
      val champ5 = Champion("champ5", Set(role2), 1)
      val champ6 = Champion("champ6", Set(role3), 1)
      val allChampions = Seq(champ1, champ2, champ3, champ4, champ5, champ6)

      val result1 =
        search(allChampions, 3, 2, requiredRoleCounts = allRoles.map(_ -> 0).toMap.updated(role2, 3))
      result1 should not be empty
      result1.get should ===(Composition(Set(champ2, champ4, champ5)))
    }

    "find nothing when the number of required roles is greater than the team size" in {
      val role1 = Role("role1", Set(1))
      val role2 = Role("role2", Set(1))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role1), 1)
      val champ3 = Champion("champ3", Set(role1), 1)
      val champ4 = Champion("champ4", Set(role1), 1)
      val allChampions = Seq(champ1, champ2, champ3, champ4)

      searchWithMinRoleThresholds(allChampions, 1, requiredRoles = Set(role1, role2)) should ===(None)
    }

    "find nothing when the number of required roles cannot be satisfied because the count threshold cannot be reached" in {
      val role1 = Role("role1", Set(3))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role1), 1)
      val champ3 = Champion("champ3", Set(role1), 1)
      val allChampions = Seq(champ1, champ2, champ3)

      searchWithMinRoleThresholds(allChampions, 2, requiredRoles = Set(role1)) should ===(None)
    }

    "find nothing when the number of required roles cannot be satisfied because the count threshold cannot be reached when thoroughness < max team size" in {
      val role1 = Role("role1", Set(2))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role1), 1)
      val allChampions = Seq(champ1, champ2)

      search(allChampions, 1, 0, Map(role1 -> 2)) shouldBe empty
    }

    "find something when role and champion requirements don't overlap but can be fulifilled" in {
      val role1 = Role("role1", Set(3))
      val role2 = Role("role2", Set(4))
      val role3 = Role("role3", Set(1))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role1), 1)
      val champ3 = Champion("champ3", Set(role1), 1)
      val champ4 = Champion("champ4", Set(role2), 1)
      val champ5 = Champion("champ5", Set(role3), 1)
      val allChampions = Seq(champ1, champ2, champ3, champ4, champ5)

      searchWithMinRoleThresholds(allChampions, 4, requiredRoles = Set(role1), requiredChampions = Set(champ4)) should ===(
        Some(Composition(Set(champ1, champ2, champ3, champ4))))
    }

    "find 6 cyber, 2 infil in real dataset" in {
      search(data.set3_0.champions.all.toSeq,
             8,
             2,
             Map(data.set3_0.roles.Cybernetic -> 6, data.set3_0.roles.Infiltrator -> 2)) should not be empty
    }

    "find 3 blade, 2 celest, 4 chrono, 2 valk in real dataset" in {
      val result = search(
        data.set3_0.champions.all.toSeq,
        8,
        8,
        Map(data.set3_0.roles.Blademaster -> 3,
            data.set3_0.roles.Celestial -> 2,
            data.set3_0.roles.Chrono -> 4,
            data.set3_0.roles.Valkyrie -> 2)
      )
      result should not be empty
    }

    "find something in the real dataset" in {
      search(Random.shuffle(data.set3_0.champions.all.toSeq), 8, 2) should not be empty
    }

    "find the shortest path" in {
      val maxTeamSize = 4
      val h = MinRoleThresholdSearchBackend.heuristic(_, maxTeamSize)
      val d = MinRoleThresholdSearchBackend.distance _

      forAll(championPoolGen(6)) { championPool =>
        val bruteForcedShortestPaths =
          bruteForceShortestPaths(maxTeamSize, h, d, championPool).map(x => x.composition -> x.cost)
        val maybeFoundShortestPath = searchReturningCost(championPool, maxTeamSize, maxTeamSize)

        if (bruteForcedShortestPaths.isEmpty) maybeFoundShortestPath shouldBe empty
        else bruteForcedShortestPaths should contain(maybeFoundShortestPath.get)
      }
    }
  }

  "MinRoleThresholdSearchBackend" should {
    "have a heuristic that minimizes the number of unused role slots" in {
      val h = MinRoleThresholdSearchBackend.heuristic(_, 2)

      val role1 = Role("role1", Set(2))
      val role2 = Role("role2", Set(2))
      val role3 = Role("role3", Set(2))
      val role4 = Role("role4", Set(3))
      val champ1 = Champion("champ1", Set(role1, role2), 1)
      val champ2 = Champion("champ2", Set(role1, role3), 1)
      val champ3 = Champion("champ3", Set(role1, role4), 1)
      val champ4 = Champion("champ4", Set(role1, role4), 1)

      val comp1 = Composition(Set(champ1, champ2)) // 2/2 r1, 1/2 r2, 1/2 r3 => missing 2/2
      val comp2 = Composition(Set(champ3, champ4)) // 2/2 r1, 2/3 r4         => missing 1/3

      h(comp1) should be > h(comp2)
    }

    "have an admissible heuristic" in {
      val maxTeamSize = 4
      val h = MinRoleThresholdSearchBackend.heuristic(_, maxTeamSize)
      val d = MinRoleThresholdSearchBackend.distance _

      forAll(championPoolGen(6)) { championPool =>
        bruteForceShortestPaths(maxTeamSize, h, d, championPool).foreach { shortestPath =>
          shortestPath.heuristics.zipWithIndex.foreach {
            case (heuristic, index) =>
              val existingChampions = shortestPath.champions.take(index)
              val newChampion = shortestPath.champions.lift(index - 1)
              withClue(s"heuristicIndex=$index existingChampions=$existingChampions newChampion=$newChampion") {
                val cost = shortestPath.distances.drop(index).sum
                heuristic should be <= cost
              }
          }
        }
      }
    }
  }

  val roleNumbers = Iterator.from(0)
  val champNumbers = Iterator.from(0)
  implicit val arbRole: Arbitrary[Role] = Arbitrary {
    for {
      thresholds <- Gen.containerOfN[Set, Int](2, Gen.choose(1, 6)).filter(_.nonEmpty)
      id = roleNumbers.next()
    } yield (Role(s"role$id", thresholds))
  }
  implicit val arbChampion: Arbitrary[Champion] = Arbitrary {
    for {
      role1 <- arbitrary[Role]
      role2 <- arbitrary[Role]
      cost <- Gen.choose(1, 5)
      id = champNumbers.next()
    } yield Champion(s"champ$id", Set(role1, role2), cost)
  }
  def championPoolGen(size: Int): Gen[Seq[Champion]] =
    Gen.containerOfN[Seq, Champion](size, arbitrary[Champion]).filter(_.nonEmpty)

  final case class Path(composition: Composition,
                        champions: Seq[Champion],
                        heuristics: Seq[Int],
                        distances: Seq[Int],
                        cost: Int)
  def bruteForceShortestPaths(maxTeamSize: Int,
                              h: Composition => Int,
                              d: (Composition, Champion) => Int,
                              championPool: Seq[Champion]): Seq[Path] = {
    championPool
      .combinations(maxTeamSize)
      .flatMap(_.permutations)
      .map { champions =>
        champions.foldLeft(Path(Composition.empty, Seq.empty[Champion], Seq(h(Composition.empty)), Seq(0), 0)) {
          case (Path(composition, champions, heuristics, distances, cost), champion) =>
            Path(
              composition.add(champion),
              champions :+ champion,
              heuristics :+ h(composition.add(champion)),
              distances :+ d(composition, champion),
              cost + d(composition, champion)
            )
        }
      }
      .toSeq
      .groupBy(_.cost)
      .minByOption(_._1)
      .map(_._2)
      .getOrElse(Seq.empty)
  }
}
