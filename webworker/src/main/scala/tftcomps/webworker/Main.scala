package tftcomps.webworker

import tftcomps.domain.{CompositionConfig, data, search}

import scala.util.Random

object Main extends App {
  scalajs.js.Dynamic.global.updateDynamic("onmessage") { (event: scalajs.js.Dynamic) =>
    import io.circe.parser._
    import io.circe.syntax._
    import tftcomps.domain.codec._

    val compositionConfig = decode[CompositionConfig](event.data.asInstanceOf[String]).toTry.get

    val results = LazyList(0 until 500: _*).flatMap { _ =>
      // Shuffle champion list because the order of champions influences the result.
      search(
        Random.shuffle(data.champions.all.toSeq).filter(_.cost <= compositionConfig.maxChampionCost),
        compositionConfig.maxTeamSize,
        compositionConfig.requiredRoles,
        compositionConfig.requiredChampions
      ).take(1)
    }

    results.foreach(result => scalajs.js.Dynamic.global.postMessage(result.asJson.noSpaces))
  }
}
