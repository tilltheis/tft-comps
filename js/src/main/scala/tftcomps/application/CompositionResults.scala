package tftcomps.application

import japgolly.scalajs.react.{React, ScalaFnComponent}
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.domain.Composition

object CompositionResults {

  final case class Props(compositions: Set[(Composition, Double)])

  val Component = ScalaFnComponent[Props] { props =>
    if (props.compositions.isEmpty) <.p("No results.")
    else {
      val scores = props.compositions.map(_._2)
      <.div(
        <.p(
          s"Found ${props.compositions.size} compositions between ${(scores.min * 100).toInt}% and ${(scores.max * 100).toInt}% quality."),
        <.dl(
          ^.display := "flex",
          ^.flexDirection := "column",
          ^.flexWrap := "wrap",
          ^.alignContent := "flex-start",
          ^.height := 2.rem,
          props.compositions.groupBy(_._2).toTagMod {
            case (distance, comps) =>
              React.Fragment(
                <.dt(^.width := 10.rem, ^.height := 1.rem, ^.fontWeight := "bold", s"${(distance * 100).toInt}%"),
                <.dd(^.width := 10.rem, ^.height := 1.rem, ^.margin := 0.rem, comps.size)
              )
          }
        ),
        <.ol(
          ^.listStyle := "none",
          ^.paddingLeft := 0.rem,
          props.compositions.toSeq
            .sortBy(-_._2)
            .toTagMod {
              case (composition, score) =>
                <.li(
                  ^.key := composition.champions.hashCode,
                  ^.marginBottom := 1.rem,
                  ChampionComposition(composition, score)
                )
            }
        )
      )
    }
  }

  def apply(compositions: Set[(Composition, Double)]) = Component(Props(compositions))
}
