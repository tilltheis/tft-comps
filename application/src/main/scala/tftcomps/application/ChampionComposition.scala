package tftcomps.application

import japgolly.scalajs.react.ScalaFnComponent
import japgolly.scalajs.react.vdom.Attr
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.application.ChampionComposition.Color
import tftcomps.domain.{Composition, Role}

object ChampionComposition {

  final case class Props(composition: Composition, quality: Double, compositionConfig: UiCompositionConfig)

  sealed abstract class Color(val value: String)
  object Color {
    case object Bronze extends Color("bronze")
    case object Silver extends Color("silver")
    case object Gold extends Color("gold")
    case object Chromatic extends Color("chromatic")
  }
  def roleStackColor(role: Role, count: Int): Option[Color] = {
    def go(thresholds: (Int, Color)*): Option[Color] = thresholds.sortBy(-_._1).find(count >= _._1).map(_._2)
    import Color._
    import tftcomps.domain.data.CurrentSet.roles._

    // generate this from the official data (https://developer.riotgames.com/docs/tft#static-data_current-set) using
    // jq -r '.[] | "case \(.name) => go(\(.sets | map("\(.min) -> \(.style[0:1] | ascii_upcase)\(.style[1:])") | join(", ")))"' traits.json
    role match {
      case Assassin     => go(2 -> Bronze, 4 -> Gold, 6 -> Chromatic)
      case Astral       => go(3 -> Bronze, 6 -> Gold, 9 -> Chromatic)
      case Bard         => go(1 -> Gold)
      case Bruiser      => go(2 -> Bronze, 4 -> Silver, 8 -> Gold)
      case Cannoneer    => go(2 -> Bronze, 3 -> Silver, 4 -> Gold, 5 -> Chromatic)
      case Cavalier     => go(2 -> Bronze, 3 -> Silver, 4 -> Gold, 5 -> Chromatic)
      case Dragon       => go(1 -> Gold)
      case Dragonmancer => go(3 -> Bronze, 6 -> Gold, 9 -> Chromatic)
      case Evoker       => go(2 -> Bronze, 4 -> Gold, 6 -> Chromatic)
      case Guardian     => go(2 -> Bronze, 4 -> Gold, 6 -> Chromatic)
      case Guild        => go(1 -> Bronze, 3 -> Silver, 5 -> Gold, 6 -> Chromatic)
      case Jade         => go(3 -> Bronze, 6 -> Silver, 9 -> Gold, 12 -> Chromatic)
      case Legend       => go(3 -> Gold)
      case Mage         => go(3 -> Bronze, 5 -> Silver, 7 -> Gold, 9 -> Chromatic)
      case Mirage       => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case Mystic       => go(2 -> Bronze, 3 -> Silver, 4 -> Gold, 5 -> Chromatic)
      case Ragewing     => go(3 -> Bronze, 6 -> Silver, 9 -> Gold)
      case Revel        => go(2 -> Bronze, 3 -> Silver, 4 -> Gold, 5 -> Chromatic)
      case Scalescorn   => go(2 -> Bronze, 4 -> Gold, 6 -> Chromatic)
      case Shapeshifter => go(2 -> Bronze, 4 -> Silver, 6 -> Gold)
      case Shimmerscale => go(3 -> Bronze, 5 -> Silver, 7 -> Gold, 9 -> Chromatic)
      case SpellThief   => go(1 -> Gold)
      case Starcaller   => go(1 -> Gold)
      case Swiftshot    => go(2 -> Bronze, 4 -> Gold, 6 -> Chromatic)
      case Tempest      => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case Trainer      => go(2 -> Bronze, 3 -> Gold)
      case Warrior      => go(2 -> Bronze, 4 -> Gold, 6 -> Chromatic)
      case Whispers     => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case _            => None
    }
  }

  val Component = ScalaFnComponent[Props] { props =>
    <.div(
      ^.className := "composition",
      <.h3(^.className := "synergy", s"${(props.composition.synergyPercentage * 100).toInt}%", <.small(" synergy")),
      <.ol(
        ^.className := "composition-roles",
        props.composition.roleCounts.toSeq
          .sortBy {
            case (role, count) =>
              // 1. role stack percentage, 2. count, 3. name
              (role.stackingBonusThresholds.filter(_ <= count).maxOption.fold(Double.MaxValue) { max =>
                -(role.stackingBonusThresholds.toSeq.sorted
                  .indexOf(max) + 1).toDouble / role.stackingBonusThresholds.size
              }, -count, role.name)
          }
          .toTagMod {
            case (role, count) =>
              <.li(
                ^.className := "role",
                ^.className := role.name.toLowerCase.replaceAll("[^a-z]", ""),
                ^.className := roleStackColor(role, count).map(_.value).getOrElse(""),
                ^.classSet(
                  "is-required" -> props.compositionConfig.requiredRoles.filter(_._2 > 0).keySet.contains(role)),
                Attr[String]("data-name") := s"$count ${role.name}",
                s"$count ${role.name}"
              )
          }
      ),
      <.ul(
        ^.className := "champions",
        props.composition.champions.toSeq.sortBy(_.name).toTagMod { champion =>
          <.li(
            ^.className := "champion",
            ^.className := champion.name.toLowerCase.replaceAll("[^a-z]", ""),
            ^.classSet("is-required" -> props.compositionConfig.requiredChampions.contains(champion)),
            <.h3(^.className := "name", champion.name),
            <.ul(
              ^.className := "champion-roles",
              champion.roles.toSeq
                .sortBy(_.name)
                .toTagMod(role =>
                  <.li(
                    ^.className := "role",
                    ^.className := role.name.toLowerCase.replaceAll("[^a-z]", ""),
                    ^.classSet(
                      "is-required" -> props.compositionConfig.requiredRoles.filter(_._2 > 0).keySet.contains(role)),
                    role.name
                ))
            )
          )
        }
      )
    )
  }

  def apply(composition: Composition, quality: Double, compositionConfig: UiCompositionConfig) =
    Component(Props(composition, quality, compositionConfig))
}
