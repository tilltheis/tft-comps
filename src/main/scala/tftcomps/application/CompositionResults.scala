package tftcomps.application

import japgolly.scalajs.react.ScalaFnComponent
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.OldMain.compositionDescription
import tftcomps.domain.Composition

object CompositionResults {

  final case class Props(compositions: Set[Composition])

  val Component = ScalaFnComponent[Props] { props =>
    if (props.compositions.isEmpty) <.div("No results.")
    else {
      val worths = props.compositions.map(_.worth)
      <.div(
        <.p(s"Found ${props.compositions.size} compositions with scores between ${worths.max} and ${worths.min}."),
        <.ol(
          props.compositions.toSeq
            .sortBy(-_.worth)
            .toTagMod(composition =>
              <.li(^.key := composition.champions.hashCode, compositionDescription(composition))))
      )
    }
  }

  def apply(compositions: Set[Composition]) = Component(Props(compositions))
}
