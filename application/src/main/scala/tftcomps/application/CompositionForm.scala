package tftcomps.application

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaFnComponent}
import tftcomps.domain.{CompositionConfig, Role}

object CompositionForm {
  final case class Props(compositionConfig: CompositionConfig, onCompositionConfigChange: CompositionConfig => Callback)

  val Component = ScalaFnComponent[Props] { props =>
    def numberSlider(title: String,
                     possibleValues: Range,
                     selectedValue: Int,
                     maybeExplanation: Option[VdomNode] = None)(onChange: Int => Callback) = <.label(
      ^.marginBottom := 0.5.rem,
      ^.display := "flex",
      ^.alignItems := "center",
      <.div(^.width := 10.rem, s"$title:"),
      <.input(
        ^.width := 9.rem,
        ^.marginRight := 1.rem,
        ^.`type` := "range",
        ^.min := possibleValues.min,
        ^.max := possibleValues.max,
        ^.value := selectedValue,
        ^.onChange ==> ((e: ReactEventFromInput) => onChange(e.target.value.toInt))
      ),
      <.div(^.width := 10.rem, s" $selectedValue"),
      maybeExplanation
    )

    def checkboxSet[A](title: String, possibleValues: Set[A], selectedValues: Set[A])(stringProjection: A => String)(
        onChange: Set[A] => Callback) = <.div(
      ^.marginBottom := 0.5.rem,
      ^.display := "flex",
      <.div(^.width := 10.rem, s"$title:"),
      <.div(
        ^.columnWidth := 10.rem,
        ^.columnCount := "9",
        ^.columnGap := 0.rem,
        possibleValues.toSeq
          .sortBy(stringProjection)
          .toTagMod { value =>
            val isSelected = selectedValues.contains(value)
            <.label(
              ^.display := "block",
              <.input(
                ^.`type` := "checkbox",
                ^.value := stringProjection(value),
                ^.checked := isSelected,
                ^.onChange ==> { (e: ReactEventFromInput) =>
                  onChange(if (e.target.checked) selectedValues + value else selectedValues - value)
                }
              ),
              <.span(^.fontWeight := (if (isSelected) "bold" else "normal"), stringProjection(value))
            )
          }
      )
    )

    def requiredRolesField(title: String, values: Map[Role, Int])(onChange: Map[Role, Int] => Callback) = <.div(
      ^.marginBottom := 0.5.rem,
      ^.display := "flex",
      <.div(^.width := 10.rem, s"$title:"),
      <.div(
        ^.columnWidth := 9.rem,
        ^.columnCount := "9",
        ^.columnGap := 1.rem,
        values.toSeq
          .sortBy(_._1.name)
          .toTagMod {
            case (role, count) =>
              val isChanged = count > 0
              <.div(
                ^.pageBreakInside := "avoid",
                ^.marginBottom := 0.5.rem,
                <.label(
                  ^.display := "block",
                  <.div(^.fontWeight := (if (isChanged) "bold" else "normal"),
                        s"$count/${role.stackingBonusThresholds.max} ${role.name}"),
                  <.input(
                    ^.margin := 0.rem,
                    ^.width := "100%",
                    ^.display := "block",
                    ^.`type` := "range",
                    ^.min := 0,
                    ^.max := role.stackingBonusThresholds.max,
                    ^.step := role.stackingBonusThresholds.tail.headOption
                      .map(_ - role.stackingBonusThresholds.head)
                      .getOrElse(role.stackingBonusThresholds.last),
                    ^.value := count,
                    ^.onChange ==> ((e: ReactEventFromInput) => onChange(values.updated(role, e.target.value.toInt)))
                  )
                )
              )
          }
      )
    )

    <.div(
      numberSlider("Max Team Size", 1 to 10, props.compositionConfig.maxTeamSize)(x =>
        props.onCompositionConfigChange(props.compositionConfig.copy(maxTeamSize = x))),
      numberSlider("Max Champion Cost", 1 to 5, props.compositionConfig.maxChampionCost)(x =>
        props.onCompositionConfigChange(props.compositionConfig.copy(maxChampionCost = x))),
      requiredRolesField("Required Traits", props.compositionConfig.requiredRoles)(x =>
        props.onCompositionConfigChange(props.compositionConfig.copy(requiredRoles = x))),
      checkboxSet("Required Champions",
                  tftcomps.domain.data.CurrentSet.champions.all,
                  props.compositionConfig.requiredChampions)(_.name)(x =>
        props.onCompositionConfigChange(props.compositionConfig.copy(requiredChampions = x))),
      numberSlider(
        "Search thoroughness",
        0 to 10,
        props.compositionConfig.searchThoroughness,
        Some(
          <.i(
            "Increasing this ",
            <.i(^.fontStyle := "normal", "might"),
            " result in finding results where none could be found before. ",
            "Usually, it just leads to slower searches with even fewer (but good) results."
          ))
      )(x => props.onCompositionConfigChange(props.compositionConfig.copy(searchThoroughness = x)))
    )
  }

  def apply(compositionConfig: CompositionConfig, onCompositionSettingsChange: CompositionConfig => Callback) =
    Component(Props(compositionConfig, onCompositionSettingsChange))
}
