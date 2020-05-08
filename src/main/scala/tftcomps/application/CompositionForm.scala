package tftcomps.application

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ScalaFnComponent, ReactEventFromInput}

object CompositionForm {
  final case class Props(compositionConfig: CompositionConfig, onCompositionConfigChange: CompositionConfig => Callback)

  val Component = ScalaFnComponent[Props] { props =>
    def numberSlider(title: String, currentValue: Int, maxValue: Int)(onChange: Int => Callback) = <.label(
      ^.marginBottom := ".5rem",
      ^.display := "flex",
      ^.alignItems := "center",
      <.span(^.display := "inline-block", ^.width := "10rem", s"$title:"),
      <.input(
        ^.width := "10rem",
        ^.`type` := "range",
        ^.min := "1",
        ^.max := maxValue.toString,
        ^.value := currentValue.toString,
        ^.onChange ==> ((e: ReactEventFromInput) => onChange(e.target.value.toInt))
      ),
      s" $currentValue"
    )

    def checkboxSet(title: String, values: Set[String]) = <.div(
      ^.marginBottom := ".5rem",
      ^.display := "flex",
      ^.flexDirection := "row",
      <.span(
        ^.display := "inline-block",
        ^.width := "11rem",
        s"$title:"
      ),
      <.span(
        ^.display := "inline-block",
        ^.width := "100%",
        ^.columnWidth := "10rem",
        ^.columnGap := "0",
        values.toSeq.sorted
          .toTagMod(value => <.label(^.display := "blocK", <.input(^.`type` := "checkbox", ^.value := value), value))
      )
    )

    <.div(
      numberSlider("Max Team Size", currentValue = props.compositionConfig.maxTeamSize, maxValue = 10)(x =>
        props.onCompositionConfigChange(props.compositionConfig.copy(maxTeamSize = x))),
      numberSlider("Max Champion Cost", currentValue = props.compositionConfig.maxChampionCost, maxValue = 5)(x =>
        props.onCompositionConfigChange(props.compositionConfig.copy(maxChampionCost = x))),
      checkboxSet("Required Roles", tftcomps.domain.data.roles.all.map(_.name)),
      checkboxSet("Required Champions", tftcomps.domain.data.champions.all.map(_.name)),
    )
  }

  def apply(compositionConfig: CompositionConfig, onCompositionSettingsChange: CompositionConfig => Callback) =
    Component(Props(compositionConfig, onCompositionSettingsChange))
}
