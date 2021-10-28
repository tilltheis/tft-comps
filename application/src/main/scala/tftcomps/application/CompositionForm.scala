package tftcomps.application

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaFnComponent}
import tftcomps.domain.{Champion, CompositionConfig, Role}

object CompositionForm {
  final case class Props(compositionConfig: CompositionConfig, onCompositionConfigChange: CompositionConfig => Callback)

  val Component = ScalaFnComponent[Props] { props =>
    def numberSlider(title: String,
                     possibleValues: Range,
                     selectedValue: Int,
                     maybeExplanation: Option[VdomNode] = None)(onChange: Int => Callback) = <.label(
      ^.className := "number-slider",
      <.div(^.className := "title", s"$title:"),
      <.input(
        ^.className := "input",
        ^.`type` := "range",
        ^.min := possibleValues.min,
        ^.max := possibleValues.max,
        ^.value := selectedValue,
        ^.onChange ==> ((e: ReactEventFromInput) => onChange(e.target.value.toInt))
      ),
      <.div(^.className := "value", s" $selectedValue"),
      maybeExplanation
    )

    def requiredChampionsField(title: String, possibleValues: Set[Champion], selectedValues: Set[Champion])(
        onChange: Set[Champion] => Callback) = <.div(
      ^.className := "champion-filter",
      <.div(^.className := "title", s"$title:"),
      <.div(
        ^.className := "options",
        possibleValues.toSeq
          .sortBy(_.name)
          .toTagMod { champion =>
            val isSelected = selectedValues.contains(champion)
            <.label(
              ^.className := "champion",
              ^.className := champion.name.toLowerCase.replaceAll("[^a-z]", ""),
              ^.classSet("is-selected" -> isSelected),
              <.input(
                ^.`type` := "checkbox",
                ^.value := champion.name,
                ^.checked := isSelected,
                ^.onChange ==> { (e: ReactEventFromInput) =>
                  onChange(if (e.target.checked) selectedValues + champion else selectedValues - champion)
                }
              ),
              champion.name
            )
          }
      )
    )

    def requiredRolesField(title: String, values: Map[Role, Int])(onChange: Map[Role, Int] => Callback) = <.div(
      ^.className := "role-filter",
      <.div(^.className := "title", s"$title:"),
      <.div(
        ^.className := "options",
        values.toSeq
          .sortBy(_._1.name)
          .toTagMod {
            case (role, count) =>
              val isChanged = count > 0
              <.div(
                ^.className := "role",
                ^.className := role.name.toLowerCase.replaceAll("[^a-z]", ""),
                ^.className := (if (isChanged) "is-changed" else ""),
                <.label(
                  <.div(s"$count/${role.stackingBonusThresholds.max} ${role.name}"),
                  <.input(
                    ^.`type` := "range",
                    ^.min := 0,
                    ^.max := role.stackingBonusThresholds.size, // +1 because 0 is not included in thresholds
                    ^.value := role.stackingBonusThresholds.toSeq.sorted.indexOf(count) + 1,
                    ^.onChange ==> { (e: ReactEventFromInput) =>
                      val newCount =
                        role.stackingBonusThresholds.toSeq.sorted.lift(e.target.value.toInt - 1).getOrElse(0)
                      onChange(values.updated(role, newCount))
                    }
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
      requiredChampionsField("Required Champions",
                             tftcomps.domain.data.CurrentSet.champions.all,
                             props.compositionConfig.requiredChampions)(x =>
        props.onCompositionConfigChange(props.compositionConfig.copy(requiredChampions = x))),
      numberSlider(
        "Search thoroughness",
        0 to 10,
        props.compositionConfig.searchThoroughness,
        Some(
          <.i(
            "Increasing this ",
            <.i("might"),
            " result in finding results where none could be found before. ",
            "Usually, it just leads to slower searches with even fewer (but good) results."
          ))
      )(x => props.onCompositionConfigChange(props.compositionConfig.copy(searchThoroughness = x)))
    )
  }

  def apply(compositionConfig: CompositionConfig, onCompositionSettingsChange: CompositionConfig => Callback) =
    Component(Props(compositionConfig, onCompositionSettingsChange))
}
