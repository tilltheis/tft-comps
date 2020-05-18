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
    def go(times: Int): Unit = if (times > 0) {
      val maybeComposition = search(
        Random.shuffle(data.champions.all.toSeq).filter(_.cost <= compositionConfig.maxChampionCost),
        compositionConfig.maxTeamSize,
        compositionConfig.requiredRoles,
        compositionConfig.requiredChampions
      )
      maybeComposition.foreach(composition => scalajs.js.Dynamic.global.postMessage(composition.asJson.noSpaces))
      scalajs.js.timers.setTimeout(0)(go(times - 1)) // give the computer time to breath
    }
    go(500)
  }
}
