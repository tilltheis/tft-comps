package tftcomps.domain

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Random

class Test extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
  "Composition" when {
    "score" should {
      val role1 = Role("role1", Set(1))
      val role2 = Role("role2", Set(2))
      val role3 = Role("role3", Set(3))
      val role24 = Role("role24", Set(2, 4))

      "give non empty compositions a rating above zero" in {
        Composition(Set.empty).score should ===(0)
        Composition(Set(Champion("champ", Set(role2), 1))).score should ===(1)
      }

      "give champions belonging the same role a multiplier according to the reached role level threshold" in {
        Composition(Set(Champion("champ1", Set(role1), 1))).score should ===(1) // 1 champ thresholds don't count
        Composition(Set(Champion("champ1", Set(role2), 1), Champion("champ2", Set(role2), 1))).score should ===(
          2 * 12 + 2) // max threshold reached
        Composition(Set(Champion("champ1", Set(role3), 1), Champion("champ2", Set(role3), 1))).score should ===(2) // no threshold reached
        Composition(
          Set(Champion("champ1", Set(role3), 1),
              Champion("champ2", Set(role3), 1),
              Champion("champ3", Set(role3), 1),
              Champion("champ4", Set(role3), 1))).score should ===(4 * 12 + 4) // max threshold reached
        Composition(
          Set(Champion("champ1", Set(role24), 1),
              Champion("champ2", Set(role24), 1),
              Champion("champ3", Set(role24), 1))).score should ===(3 * 6 + 3) // half threshold reached
        Composition(
          Set(Champion("champ1", Set(role24), 1),
              Champion("champ2", Set(role24), 1),
              Champion("champ3", Set(role24), 1),
              Champion("champ4", Set(role24), 1))).score should ===(4 * 12 + 4) // max threshold reached
      }

      "only count roles that have reached a stacking threshold greater than one" in {
        Composition(Set(Champion("champ1", Set(role1), 1))).score should ===(1)
        Composition(Set(Champion("champ2", Set(role2), 1))).score should ===(1)
        Composition(Set(Champion("champ3", Set(role1, role2), 1))).score should ===(1)
        Composition(Set(Champion("champ1", Set(role1), 1), Champion("champ2", Set(role2), 1))).score should ===(2)
        Composition(Set(Champion("champ1", Set(role1), 1),
                        Champion("champ2", Set(role2), 1),
                        Champion("champ3", Set(role1), 1))).score should ===(3)
        Composition(Set(Champion("champ1", Set(role1), 1),
                        Champion("champ2", Set(role2), 1),
                        Champion("champ3", Set(role2), 1))).score should ===(27)
      }
    }
  }

  "PriorityQueue" when {
    "enqueue and dequeue" should {
      "use the order" in {
        val q123 = PriorityQueue(3, 1, 2)
        val (maybe3, q12) = q123.dequeueMax
        maybe3 should ===(Some(3))
        val (maybe2, q1) = q12.dequeueMax
        maybe2 should ===(Some(2))
        val (maybe1, q) = q1.dequeueMax
        maybe1 should ===(Some(1))
        q.dequeueMax._1 should ===(None)
      }

      "work with random inserts" in {
        val qEmpty = PriorityQueue.empty[Int]
        val q12 = qEmpty.enqueue(1).enqueue(2)
        val (maybe2, q1) = q12.dequeueMax
        maybe2 should ===(Some(2))
        val (maybe1, q) = q1.dequeueMax
        maybe1 should ===(Some(1))
        q.dequeueMax._1 should ===(None)
      }

      "work with elements that have same prio" in {
        val q12 = PriorityQueue("1", "2")(Ordering.by(_.length))
        val (maybe1Or2_1, q1Or2) = q12.dequeueMax
        val (maybe1Or2_2, _) = q1Or2.dequeueMax

        (maybe1Or2_1, maybe1Or2_2) should {
          equal((Some("1"), Some("2"))) or
            equal((Some("2"), Some("1")))
        }
      }
    }
  }

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
//          .take(250)
        .toVector
          .map {
            case (composition, length) =>
              s"length=$length size=${composition.size} ${compositionDescription(composition)}"
          }
          .mkString("\n"))
      result.take(1).toVector should not be empty
    }
  }

  "AnyRoleThresholdSearchBackend" should {
    val d = AnyRoleThresholdSearchBackend.distance _
    val h = AnyRoleThresholdSearchBackend.heuristic(_, 3)

    val role1 = Role("role1", Set(2))
    val role2 = Role("role2", Set(2))
    val role3 = Role("role3", Set(2))
    val champ1 = Champion("champ1", Set(role1), 1)
    val champ2 = Champion("champ2", Set(role2), 1)
    val champ3 = Champion("champ3", Set(role1), 1)
    val champ4 = Champion("champ4", Set(role3), 1)
    val champ5 = Champion("champ5", Set(role2), 1)
    val comp0 = Composition(Set.empty)
    val comp1 = Composition(Set(champ1))
    val comp2 = Composition(Set(champ1, champ2))
    val comp3 = Composition(Set(champ1, champ3))
    val comp4 = Composition(Set(champ1, champ2, champ4))
    val comp5 = Composition(Set(champ1, champ2, champ5))

    // does it, though?
    "heuristic should always be smaller for bigger teams than for smaller teams " ignore {
      h(comp0) should be > h(comp1)
      h(comp1) should be > h(comp2)
      h(comp1) should be > h(comp3)
      h(comp3) should be > h(comp4)
      h(comp3) should be > h(comp5)
      h(comp4) should be > h(comp5)
    }

    "heuristic should follow a formula" in {
      // max( numberOfOpenRoles / maxRolesPerChampion, numberOfMissingChampions )
      h(comp0) should ===(3) // team size
      h(comp1) should ===(2) // team size
      h(comp2) should ===(1) // open thresholds and team size
      h(comp3) should ===(1) // team size
      h(comp4) should ===(1) // open thresholds
      h(comp5) should ===(0) // perfect
    }

    "distance should follow a formula" in {
      // 1 + open thresholds
      d(comp0, champ1) should ===(1 + 1)
      d(comp1, champ2) should ===(1 + 2)
      d(comp1, champ3) should ===(1)
      d(comp2, champ4) should ===(1 + 3)
      d(comp2, champ5) should ===(1 + 1)
    }
  }
}
