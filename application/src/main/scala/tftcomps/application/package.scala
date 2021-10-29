package tftcomps

import tftcomps.domain.{Champion, CompositionConfig, Role}

package application {

  final case class UiCompositionConfig(maxTeamSize: Int,
                                       maxChampionCost: Int,
                                       requiredRoles: Map[Role, Int],
                                       requiredChampions: Set[Champion]) {
    def toCompositionConfig(searchThoroughness: Int): CompositionConfig =
      CompositionConfig(maxTeamSize, maxChampionCost, requiredRoles, requiredChampions, searchThoroughness)
  }
}
