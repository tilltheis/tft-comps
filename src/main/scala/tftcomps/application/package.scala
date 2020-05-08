package tftcomps

import tftcomps.domain.Champion

package object application {
  final case class CompositionConfig(maxTeamSize: Int, maxChampionCost: Int, requiredChampions: Set[Champion])
}
