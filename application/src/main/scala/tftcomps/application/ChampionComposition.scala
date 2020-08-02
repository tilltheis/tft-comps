package tftcomps.application

import japgolly.scalajs.react.ScalaFnComponent
import japgolly.scalajs.react.vdom.Attr
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.domain.{Composition, Role}

object ChampionComposition {

  final case class Props(composition: Composition, quality: Double)

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
    role match {
      case Astro         => go(3 -> Gold)
      case Battlecast    => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case Blademaster   => go(3 -> Bronze, 6 -> Gold, 9 -> Chromatic)
      case Blaster       => go(2 -> Bronze, 4 -> Gold)
      case Brawler       => go(2 -> Bronze, 4 -> Gold)
      case Celestial     => go(2 -> Bronze, 4 -> Gold, 6 -> Chromatic)
      case Chrono        => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case Cybernetic    => go(3 -> Bronze, 6 -> Gold)
      case DarkStar      => go(3 -> Bronze, 6 -> Gold, 9 -> Chromatic)
      case Demolitionist => go(2 -> Gold)
      case Infiltrator   => go(2 -> Bronze, 4 -> Gold, 6 -> Chromatic)
      case ManaReaver    => go(2 -> Gold)
      case MechPilot     => go(3 -> Gold)
      case Mercenary     => go(1 -> Gold)
      case Mystic        => go(2 -> Bronze, 4 -> Gold)
      case Paragon       => go(1 -> Gold)
      case Protector     => go(2 -> Bronze, 4 -> Gold, 6 -> Chromatic)
      case Rebel         => go(3 -> Bronze, 6 -> Gold, 9 -> Chromatic)
      case Sniper        => go(2 -> Bronze, 4 -> Gold)
      case Sorcerer      => go(2 -> Bronze, 4 -> Silver, 6 -> Gold, 8 -> Chromatic)
      case SpacePirate   => go(2 -> Bronze, 4 -> Gold)
      case StarGuardian  => go(3 -> Bronze, 6 -> Gold, 9 -> Chromatic)
      case Starship      => go(1 -> Gold)
      case Vanguard      => go(2 -> Bronze, 4 -> Gold)
      case _             => None
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
                ^.className := Set("role",
                                   role.name.toLowerCase.replaceAll("[^a-z]", ""),
                                   roleStackColor(role, count).map(_.value).getOrElse("")).mkString(" "),
                Attr[String]("data-name") := s"$count ${role.name}",
                s"$count ${role.name}"
              )
          }
      ),
      <.ul(
        ^.className := "champions",
        props.composition.champions.toSeq.sortBy(_.name).toTagMod { champion =>
          <.li(
            ^.className := Set("champion", champion.name.toLowerCase.replaceAll("[^a-z]", "")).mkString(" "),
            <.h3(^.className := "name", champion.name),
            <.ul(
              ^.className := "champion-roles",
              ^.paddingLeft := 0.rem,
              champion.roles.toSeq
                .sortBy(_.name)
                .toTagMod(role => <.li(^.className := Set("role", role.name.toLowerCase.replaceAll("[^a-z]", "")).mkString(" "), ^.listStyle := "none", role.name))
            )
          )
        }
      )
    )
  }

  def apply(composition: Composition, quality: Double) = Component(Props(composition, quality))
}
