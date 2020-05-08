package tftcomps.domain

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class Test extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
  "Composition" when {
    "worth" should {
      val role1 = Role("role1", Set(1))
      val role2 = Role("role2", Set(2))
      val role3 = Role("role3", Set(3))
      val role24 = Role("role24", Set(2, 4))

      "give champions belonging the same role a multiplier according to the reached role level threshold" in {
        Composition(Set(Champion("champ1", Set(role1), 1))).worth should ===(1) // 1 champ thresholds don't count
        Composition(Set(Champion("champ1", Set(role2), 1), Champion("champ2", Set(role2), 1))).worth should ===(
          2 * 12 + 2) // max threshold reached
        Composition(Set(Champion("champ1", Set(role3), 1), Champion("champ2", Set(role3), 1))).worth should ===(2) // no threshold reached
        Composition(
          Set(Champion("champ1", Set(role3), 1),
              Champion("champ2", Set(role3), 1),
              Champion("champ3", Set(role3), 1),
              Champion("champ4", Set(role3), 1))).worth should ===(4 * 12 + 4) // max threshold reached
        Composition(
          Set(Champion("champ1", Set(role24), 1),
              Champion("champ2", Set(role24), 1),
              Champion("champ3", Set(role24), 1))).worth should ===(3 * 6 + 3) // half threshold reached
        Composition(
          Set(Champion("champ1", Set(role24), 1),
              Champion("champ2", Set(role24), 1),
              Champion("champ3", Set(role24), 1),
              Champion("champ4", Set(role24), 1))).worth should ===(4 * 12 + 4) // max threshold reached
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

      search(allChampions, 3, Set(champ1, champ2)).head should {
        equal(Composition(Set(champ1, champ2, champ3))) or
          equal(Composition(Set(champ1, champ2, champ4)))
      }
    }
  }
}
