package tftcomps

import tftcomps.domain.{Champion, Role}

package object application {
  final case class CompositionConfig(maxTeamSize: Int,
                                     maxChampionCost: Int,
                                     requiredRoles: Map[Role, Int],
                                     requiredChampions: Set[Champion])
}
