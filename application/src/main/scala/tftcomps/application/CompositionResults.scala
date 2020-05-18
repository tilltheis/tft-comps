package tftcomps.application

import japgolly.scalajs.react.{React, ScalaFnComponent}
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.domain.Composition

object CompositionResults {

  final case class Props(compositions: Seq[Composition])

  val Component = ScalaFnComponent[Props] { props =>
    if (props.compositions.isEmpty) <.p("No results.")
    else {
      val synergyPercentages = props.compositions.map(_.synergyPercentage.*(100).toInt)
      <.div(
        <.p(
          s"Found top ${props.compositions.size} compositions between ${synergyPercentages.min}% and ${synergyPercentages.max}% synergy."),
        <.dl(
          ^.display := "flex",
          ^.flexDirection := "column",
          ^.flexWrap := "wrap",
          ^.alignContent := "flex-start",
          ^.height := 2.rem,
          props.compositions.groupBy(_.synergyPercentage.*(100).toInt).toSeq.reverse.toTagMod {
            case (synergyPercentage, comps) =>
              React.Fragment(
                <.dt(^.width := 10.rem, ^.height := 1.rem, ^.fontWeight := "bold", s"$synergyPercentage%"),
                <.dd(^.width := 10.rem, ^.height := 1.rem, ^.margin := 0.rem, comps.size)
              )
          }
        ),
        <.ol(
          ^.listStyle := "none",
          ^.paddingLeft := 0.rem,
          props.compositions
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

  def apply(compositions: Seq[Composition]) = Component(Props(compositions))
}
