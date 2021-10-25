package tftcomps.application

import japgolly.scalajs.react.{React, ScalaFnComponent}
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.domain.{Composition, CompositionConfig}

object CompositionResults {

  final case class Props(compositions: Seq[Composition], searchResultCount: Int, compositionConfig: CompositionConfig)

  val Component = ScalaFnComponent[Props] { props =>
    if (props.compositions.isEmpty) <.p(s"No results from ${props.searchResultCount}/500 searches.")
    else {
      val synergyPercentages = props.compositions.map(_.synergyPercentage.*(100).toInt)
      <.div(
        <.p(
          s"Showing top ${props.compositions.size} compositions between ${synergyPercentages.min}% and ${synergyPercentages.max}% synergy from ${props.searchResultCount}/500 searches."),
        <.dl(
          ^.display := "flex",
          ^.flexDirection := "column",
          ^.flexWrap := "wrap",
          ^.alignContent := "flex-start",
          ^.height := 2.rem,
          props.compositions.groupBy(_.synergyPercentage.*(100).toInt).toSeq.sortBy(-_._1).toTagMod {
            case (synergyPercentage, comps) =>
              React.Fragment(
                <.dt(^.width := 10.rem, ^.height := 1.rem, ^.fontWeight := "bold", s"$synergyPercentage%"),
                <.dd(^.width := 10.rem, ^.height := 1.rem, ^.margin := 0.rem, comps.size)
              )
          }
        ),
        <.ol(
          ^.className := "compositions",
          props.compositions
            .toTagMod { composition =>
              <.li(
                ^.key := composition.champions.hashCode,
                ^.className := "composition-entry",
                ChampionComposition(composition, composition.synergyPercentage, props.compositionConfig)
              )
            }
        )
      )
    }
  }

  def apply(compositions: Seq[Composition], searchResultCount: Int, compositionConfig: CompositionConfig) =
    Component(Props(compositions, searchResultCount, compositionConfig))
}
