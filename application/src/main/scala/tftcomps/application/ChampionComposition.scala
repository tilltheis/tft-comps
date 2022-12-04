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
      case Ace          => go(1 -> Bronze, 4 -> Gold)
      case Admin        => go(2 -> Bronze, 4 -> Gold, 6 -> Chromatic)
      case Aegis        => go(2 -> Bronze, 3 -> Silver, 4 -> Gold, 5 -> Chromatic)
      case AnimaSquad   => go(3 -> Bronze, 5 -> Silver, 7 -> Gold)
      case Arsenal      => go(1 -> Gold)
      case Brawler      => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case Civilian     => go(1 -> Bronze, 3 -> Silver, 3 -> Gold)
      case Corrupted    => go(1 -> Gold)
      case Defender     => go(2 -> Bronze, 4 -> Silver, 6 -> Gold)
      case Duelist      => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case Forecaster   => go(1 -> Gold)
      case Gadgeteen    => go(3 -> Bronze, 5 -> Gold)
      case Hacker       => go(2 -> Bronze, 3 -> Gold, 4 -> Chromatic)
      case Heart        => go(2 -> Bronze, 4 -> Silver, 6 -> Gold)
      case LaserCorps   => go(3 -> Bronze, 6 -> Silver, 9 -> Gold)
      case Mascot       => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case MechaPrime   => go(3 -> Bronze, 5 -> Gold)
      case OxForce      => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case Prankster    => go(2 -> Bronze, 3 -> Gold)
      case Recon        => go(2 -> Bronze, 3 -> Silver, 4 -> Gold)
      case Renegade     => go(3 -> Gold, 6 -> Chromatic)
      case Spellslinger => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case StarGuardian => go(3 -> Bronze, 5 -> Silver, 7 -> Gold, 9 -> Chromatic)
      case Supers       => go(3 -> Gold)
      case Sureshot     => go(2 -> Bronze, 4 -> Gold)
      case Threat       => go(1 -> Chromatic)
      case Underground  => go(3 -> Bronze, 5 -> Gold)
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
