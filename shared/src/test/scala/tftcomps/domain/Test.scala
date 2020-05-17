package tftcomps.domain

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Random

class Test extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
  "search" should {
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

      search(allChampions, 2).head should {
        equal(Composition(Set(champ1, champ2))) or
          equal(Composition(Set(champ1, champ4)))
      }

      search(allChampions, 3).head should ===(Composition(Set(champ1, champ2, champ4)))
    }

    "be possible around a required set of champions" in {
      val role1 = Role("role1", Set(1))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role1), 1)
      val champ3 = Champion("champ3", Set(role1), 1)
      val champ4 = Champion("champ4", Set(role1), 1)
      val allChampions = Seq(champ1, champ2, champ3, champ4)

      val result = search(allChampions, 3, requiredChampions = Set(champ1, champ2))
      result should not be empty
      result.head should {
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

      search(allChampions, 1, requiredChampions = Set(champ1, champ2)) should ===(LazyList.empty)
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

      val result1 = search(allChampions, 3, requiredRoles = Set(role1, role2))
      result1 should not be empty
      result1.head.roles.keySet should contain allElementsOf (Set(role1, role2))

      val result2 = search(allChampions, 2, requiredRoles = Set(role3))
      result2 should not be empty
      result2.head.roles.keySet should contain allElementsOf (Set(role3))
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
        searchWithDetailedRoles(allChampions, 3, requiredRoles = allRoles.map(_ -> 0).toMap.updated(role2, 3))
      result1 should not be empty
      result1.head should ===(Composition(Set(champ2, champ4, champ5)))
    }

    "find nothing when the number of required roles is greater than the team size" in {
      val role1 = Role("role1", Set(1))
      val role2 = Role("role2", Set(1))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role1), 1)
      val champ3 = Champion("champ3", Set(role1), 1)
      val champ4 = Champion("champ4", Set(role1), 1)
      val allChampions = Seq(champ1, champ2, champ3, champ4)

      search(allChampions, 1, requiredRoles = Set(role1, role2)) should ===(LazyList.empty)
    }

    "find nothing when the number of required roles cannot be satisfied because the count threshold cannot be reached" in {
      val role1 = Role("role1", Set(3))
      val champ1 = Champion("champ1", Set(role1), 1)
      val champ2 = Champion("champ2", Set(role1), 1)
      val champ3 = Champion("champ3", Set(role1), 1)
      val allChampions = Seq(champ1, champ2, champ3)

      search(allChampions, 2, requiredRoles = Set(role1)) should ===(LazyList.empty)
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

      search(allChampions, 4, requiredRoles = Set(role1), requiredChampions = Set(champ4)) should ===(
        LazyList(Composition(Set(champ1, champ2, champ3, champ4))))
    }

    "find 6 cyber, 2 infil in real dataset" in {
      search2(data.champions.all.toSeq, 8, Map(data.roles.Cybernetic -> 6, data.roles.Infiltrator -> 2)) should not be empty
    }

    "find something in the real dataset" in {
      def compositionDescription(composition: Composition): String = {
        val rolesString =
          composition.roles.toSeq
            .sortBy { case (role, count) => (-count, role.name) }
            .map { case (role, count) => s"$count ${role.name}" }
            .mkString(", ")
        val champsString = composition.champions.map(_.name).toSeq.sorted.mkString(", ")
        s"[${composition.score}] (${composition.roles.size}) $rolesString ($champsString)"
      }
      val result = search2(Random.shuffle(data.champions.all.toSeq), 8)
      println("total result count = " + result.toVector.size)
      println(
        result
          .take(10)
          .toVector
          .map {
            case (composition, length) =>
              s"length=$length size=${composition.size} ${compositionDescription(composition)}"
          }
          .mkString("\n"))
      result.take(1).toVector should not be empty
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
  }

}
