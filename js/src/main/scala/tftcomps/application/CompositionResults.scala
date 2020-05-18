package tftcomps.application

import japgolly.scalajs.react.{React, ScalaFnComponent}
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.domain.Composition

object CompositionResults {

  final case class Props(compositions: Set[Composition])

  val Component = ScalaFnComponent[Props] { props =>
    if (props.compositions.isEmpty) <.p("No results.")
    else {
      val synergyPercentages = props.compositions.map(_.synergyPercentage)
      <.div(
        <.p(
          s"Found ${props.compositions.size} compositions between ${(synergyPercentages.min * 100).toInt}% and ${(synergyPercentages.max * 100).toInt}% synergy."),
        <.dl(
          ^.display := "flex",
          ^.flexDirection := "column",
          ^.flexWrap := "wrap",
          ^.alignContent := "flex-start",
          ^.height := 2.rem,
          props.compositions.groupBy(_.synergyPercentage).toSeq.sortBy(-_._1).toTagMod {
            case (synergyPercentage, comps) =>
              React.Fragment(
                <.dt(^.width := 10.rem,
                     ^.height := 1.rem,
                     ^.fontWeight := "bold",
                     s"${(synergyPercentage * 100).toInt}%"),
                <.dd(^.width := 10.rem, ^.height := 1.rem, ^.margin := 0.rem, comps.size)
              )
          }
        ),
        <.ol(
          ^.listStyle := "none",
          ^.paddingLeft := 0.rem,
          props.compositions.toSeq
            .sortBy(-_.synergyPercentage)
            .toTagMod { composition =>
              <.li(
                ^.key := composition.champions.hashCode,
                ^.marginBottom := 1.rem,
                ChampionComposition(composition, composition.synergyPercentage)
              )
            }
        )
      )
    }
  }

  def apply(compositions: Set[Composition]) = Component(Props(compositions))
}
