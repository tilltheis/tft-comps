package tftcomps.webworker

import tftcomps.domain.{CompositionConfig, data, search}

import scala.util.Random

object Main extends App {
  scalajs.js.Dynamic.global.updateDynamic("onmessage") { (event: scalajs.js.Dynamic) =>
    import io.circe.parser._
    import io.circe.syntax._
    import tftcomps.domain.codec._

    val compositionConfig = decode[CompositionConfig](event.data.asInstanceOf[String]).toTry.get

    // We cannot search the entire tree. It's too big. Therefore try several times w/ shuffled inputs.
    val maybeComposition = search(
      Random.shuffle(data.CurrentSet.champions.all.toSeq).filter(_.cost <= compositionConfig.maxChampionCost),
      compositionConfig.maxTeamSize,
      compositionConfig.searchThoroughness,
      compositionConfig.requiredRoles,
      compositionConfig.requiredChampions
    )
    scalajs.js.Dynamic.global.postMessage(maybeComposition.asJson.noSpaces)
  }
}
