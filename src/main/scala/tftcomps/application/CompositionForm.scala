package tftcomps.application

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaFnComponent}

object CompositionForm {
  final case class Props(compositionConfig: CompositionConfig, onCompositionConfigChange: CompositionConfig => Callback)

  val Component = ScalaFnComponent[Props] { props =>
    def numberSlider(title: String, possibleValues: Range, selectedValue: Int)(onChange: Int => Callback) = <.label(
      ^.marginBottom := ".5rem",
      ^.display := "flex",
      ^.alignItems := "center",
      <.div(^.width := "10rem", s"$title:"),
      <.input(
        ^.width := "10rem",
        ^.`type` := "range",
        ^.min := possibleValues.min,
        ^.max := possibleValues.max.toString,
        ^.value := selectedValue.toString,
        ^.onChange ==> ((e: ReactEventFromInput) => onChange(e.target.value.toInt))
      ),
      <.div(^.width := "10rem", s" $selectedValue"),
    )

    def checkboxSet[A](title: String, possibleValues: Set[A], selectedValues: Set[A])(stringProjection: A => String)(
        onChange: Set[A] => Callback) = <.div(
      ^.marginBottom := "0.5rem",
      ^.display := "flex",
      <.div(
        ^.display := "inline-block",
        ^.width := "10rem",
        s"$title:"
      ),
      <.div(
        ^.columnWidth := "10rem",
        ^.columnCount := "9",
        ^.columnGap := "0",
        possibleValues.toSeq
          .sortBy(stringProjection)
          .toTagMod(value =>
            <.label(
              ^.display := "block",
              <.input(
                ^.`type` := "checkbox",
                ^.value := stringProjection(value),
                ^.onChange ==> { (e: ReactEventFromInput) =>
                  onChange(if (e.target.checked) selectedValues + value else selectedValues - value)
                }
              ),
              stringProjection(value)
          ))
      )
    )

    <.div(
      numberSlider("Max Team Size", 1 to 10, props.compositionConfig.maxTeamSize)(x =>
        props.onCompositionConfigChange(props.compositionConfig.copy(maxTeamSize = x))),
      numberSlider("Max Champion Cost", 1 to 5, props.compositionConfig.maxChampionCost)(x =>
        props.onCompositionConfigChange(props.compositionConfig.copy(maxChampionCost = x))),
      checkboxSet("Required Roles", tftcomps.domain.data.roles.all, props.compositionConfig.requiredRoles)(_.name)(x =>
        props.onCompositionConfigChange(props.compositionConfig.copy(requiredRoles = x))),
      checkboxSet("Required Champions", tftcomps.domain.data.champions.all, props.compositionConfig.requiredChampions)(
        _.name)(x => props.onCompositionConfigChange(props.compositionConfig.copy(requiredChampions = x))),
    )
  }

  def apply(compositionConfig: CompositionConfig, onCompositionSettingsChange: CompositionConfig => Callback) =
    Component(Props(compositionConfig, onCompositionSettingsChange))
}
