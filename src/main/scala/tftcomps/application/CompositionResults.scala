package tftcomps.application

import japgolly.scalajs.react.ScalaFnComponent
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.domain.Composition

object CompositionResults {

  final case class Props(compositions: Set[Composition])

  val Component = ScalaFnComponent[Props] { props =>
    def compositionDescription(composition: Composition): String = {
      val rolesString =
        composition.roles.toSeq
          .sortBy { case (role, count) => (-count, role.name) }
          .map { case (role, count) => s"$count ${role.name}" }
          .mkString(", ")
      val champsString = composition.champions.map(_.name).toSeq.sorted.mkString(", ")
      s"[${composition.worth}] (${composition.roles.size}) $rolesString ($champsString)"
    }

    if (props.compositions.isEmpty) <.div("No results.")
    else {
      val worths = props.compositions.map(_.worth)
      <.div(
        <.p(s"Found ${props.compositions.size} compositions scored between ${worths.max} and ${worths.min} points."),
        <.ol(
          ^.listStyle := "none",
          ^.paddingLeft := "0",
          props.compositions.toSeq
            .sortBy(-_.worth)
            .toTagMod(composition =>
              <.li(^.key := composition.champions.hashCode, ^.marginBottom := "1rem", ChampionComposition(composition)))
        )
      )
    }
  }

  def apply(compositions: Set[Composition]) = Component(Props(compositions))
}
