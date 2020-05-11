package tftcomps.application

import japgolly.scalajs.react.ScalaFnComponent
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.domain.Composition

object ChampionComposition {

  final case class Props(composition: Composition, score: Int)

  val Component = ScalaFnComponent[Props] { props =>
    <.div(
      ^.display := "flex",
      <.h3(^.width := 10.rem, s"${props.score.toString} (${props.composition.score.toString})", <.small(" points")),
      <.ol(
        ^.listStyle := "none",
        ^.paddingLeft := 0.rem,
        ^.width := 10.rem,
        props.composition.roles.toSeq
          .sortBy {
            case (role, count) =>
              // 1. role stack percentage, 2. count, 3. name
              (role.stackingBonusThresholds.filter(_ <= count).maxOption.fold(Double.MaxValue) { max =>
                -(role.stackingBonusThresholds.toSeq.sorted
                  .indexOf(max) + 1).toDouble / role.stackingBonusThresholds.size
              }, -count, role.name)
          }
          .toTagMod {
            case (role, count) =>
              <.li(
                ^.fontWeight := (if (count >= role.stackingBonusThresholds.max) "bolder"
                                 else if (count >= role.stackingBonusThresholds.min) "bold"
                                 else "normal"),
                s"${count} ${role.name}"
              )
          }
      ),
      <.ul(
        ^.display := "flex",
        ^.listStyle := "none",
        ^.paddingLeft := 0.rem,
        props.composition.champions.toSeq.sortBy(_.name).toTagMod { champion =>
          <.li(
            ^.width := 10.rem,
            <.h3(champion.name),
            <.ul(^.paddingLeft := 0.rem,
                 champion.roles.toSeq
                   .sortBy(_.name)
                   .toTagMod(role => <.li(^.listStyle := "none", role.name)))
          )
        }
      )
    )
  }

  def apply(composition: Composition, score: Int) = Component(Props(composition, score))
}
