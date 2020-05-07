package tftcomps

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class Test extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
  "Composition" when {
    "worth" should {
      val role1 = Role("role1", Set(1))
      val role3 = Role("role3", Set(3))
      val role24 = Role("role24", Set(2, 4))

      "count role champions of comps that hit role thresholds exactly twice" in {
        Composition(Set(Champion("champ1", Set(role1), 1))).worth should ===(2)
        Composition(Set(Champion("champ1", Set(role3), 1))).worth should ===(1)
        Composition(
          Set(Champion("champ1", Set(role3), 1),
              Champion("champ2", Set(role3), 1),
              Champion("champ3", Set(role3), 1),
              Champion("champ4", Set(role3), 1))).worth should ===(7)
        Composition(
          Set(Champion("champ1", Set(role24), 1),
              Champion("champ2", Set(role24), 1),
              Champion("champ3", Set(role24), 1),
              Champion("champ4", Set(role24), 1))).worth should ===(8)
        Composition(Set(Champion("champ1", Set(role1, role3), 1), Champion("champ2", Set(role3), 1))).worth should ===(
          4)
      }

      "sum the squares of the count of each role" ignore {
        val void = Role("void", Set(1))
        val brawler = Role("brawler", Set(1))
        val valkyrie = Role("valkyrie", Set(1))
        val infiltrator = Role("infiltrator", Set(2))
        Composition(Set(Champion("void", Set(void), 1))).worth should ===(1)
        Composition(Set(Champion("void1", Set(void), 1), Champion("void2", Set(void), 1))).worth should ===(4)
        Composition(Set(Champion("void", Set(void), 1), Champion("void_brawler", Set(void, brawler), 1))).worth should ===(
          5)
        Composition(
          Set(
            Champion("void_infiltrator", Set(void, infiltrator), 1),
            Champion("void_brawler", Set(void, brawler), 1),
            Champion("infiltrator_valkyrie", Set(infiltrator, valkyrie), 1)
          )).worth should ===(10)
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
        val (maybe1Or2, q1Or2) = q12.dequeueMax
        if (maybe1Or2.contains("1")) {
          val (maybe2, _) = q1Or2.dequeueMax
          maybe2 should ===(Some("2"))
        } else if (maybe1Or2.contains("2")) {
          val (maybe1, _) = q1Or2.dequeueMax
          maybe1 should ===(Some("1"))
        } else {
          fail(s"illegal value $maybe1Or2")
        }
      }
    }
  }
}
