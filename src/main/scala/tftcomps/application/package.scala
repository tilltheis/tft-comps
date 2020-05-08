package tftcomps

import tftcomps.domain.{Champion, Role}

package object application {
  final case class CompositionConfig(maxTeamSize: Int,
                                     maxChampionCost: Int,
                                     requiredRoles: Set[Role],
                                     requiredChampions: Set[Champion])
}
