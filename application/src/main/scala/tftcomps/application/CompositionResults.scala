package tftcomps.application

import japgolly.scalajs.react.{React, ScalaFnComponent}
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.domain.{Composition, CompositionConfig}

object CompositionResults {

  final case class Props(compositions: Seq[Composition], searchResultCount: Int, compositionConfig: CompositionConfig)

  val Component = ScalaFnComponent[Props] { props =>
    if (props.compositions.isEmpty)
      <.div(^.className := "results",
            <.p(^.className := "description", s"No results from ${props.searchResultCount}/500 searches."))
    else {
      val synergyPercentages = props.compositions.map(_.synergyPercentage.*(100).toInt)
      <.div(
        ^.className := "results",
        <.p(
          ^.className := "description",
          s"Showing top ${props.compositions.size} compositions between ${synergyPercentages.min}% and ${synergyPercentages.max}% synergy from ${props.searchResultCount}/500 searches."
        ),
        <.dl(
          ^.className := "distribution",
          props.compositions.groupBy(_.synergyPercentage.*(100).toInt).toSeq.sortBy(-_._1).toTagMod {
            case (synergyPercentage, comps) =>
              React.Fragment(
                <.dt(s"$synergyPercentage%"),
                <.dd(comps.size)
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
