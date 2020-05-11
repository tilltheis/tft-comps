package tftcomps.application

import japgolly.scalajs.react.ScalaFnComponent
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.domain.Composition

object CompositionResults {

  final case class Props(compositions: Seq[(Composition, Int)])

  val Component = ScalaFnComponent[Props] { props =>
    if (props.compositions.isEmpty) <.p("No results.")
    else {
      val scores = props.compositions.map(_._2)
      <.div(
        <.p(s"Found ${props.compositions.size} compositions scored between ${scores.max} and ${scores.min} points."),
        <.ol(
          ^.listStyle := "none",
          ^.paddingLeft := 0.rem,
          props.compositions
            .sortBy(_._2)
            .toTagMod {
              case (composition, score) =>
                <.li(^.key := composition.champions.hashCode,
                     ^.marginBottom := 1.rem,
                     ChampionComposition(composition, score))
            }
        )
      )
    }
  }

  def apply(compositions: Seq[(Composition, Int)]) = Component(Props(compositions))
}
