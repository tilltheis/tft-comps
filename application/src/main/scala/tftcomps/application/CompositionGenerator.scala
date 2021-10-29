package tftcomps.application

import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, CallbackTo, ScalaComponent}
import java.util.concurrent.atomic.AtomicInteger
import org.scalajs.dom.raw.{MessageEvent, Worker}
import tftcomps.domain.{Composition, CompositionConfig, data}

object CompositionGenerator {
  final case class State(compositionConfig: CompositionConfig,
                         compositions: Seq[Composition],
                         searchResultCount: Int,
                         workers: Seq[Worker])
  object State {
    val empty: State = State(
      CompositionConfig(maxTeamSize = 8,
                        maxChampionCost = 5,
                        requiredRoles = data.CurrentSet.roles.all.map(_ -> 0).toMap,
                        requiredChampions = Set.empty,
                        searchThoroughness = 2),
      Seq.empty,
      0,
      Seq.empty
    )
  }

  final case class Backend($ : BackendScope[Unit, State]) {
    def handleCompositionConfigChange(newCompositionConfig: CompositionConfig): Callback = {
      import io.circe.parser._
      import io.circe.syntax._
      import tftcomps.domain.codec._

      val batchSize = 10
      val remainingTaskCount = new AtomicInteger(150 - batchSize)

      val terminateWorkers = $.state.map(_.workers.foreach(_.terminate()))
      val spawnWorkers = CallbackTo {
        (0 until batchSize).map { i =>
          // fastest worker will produce most results but that's ok
          val thoroughnessOverride = Seq(0, 1, 2, 3, 4)(i % 5)
          val worker = new Worker("../../../../webworker/target/scala-2.13/tft-comps-webworker-fastopt.js")
          worker.onmessage = { (event: MessageEvent) =>
            if (remainingTaskCount.decrementAndGet() >= 0) {
              // give the computer time to breath
              scalajs.js.timers.setTimeout(0) {
                worker.postMessage(newCompositionConfig.copy(searchThoroughness = thoroughnessOverride).asJson.noSpaces)
              }
            }
            val maybeComposition = decode[Option[Composition]](event.data.asInstanceOf[String]).toTry.get
            $.modState { state =>
              val newCompositions = maybeComposition.fold(state.compositions)(state.compositions :+ _)
              state.copy(
                compositions =
                  newCompositions.distinct.sortBy(c => (-c.synergyPercentage, -c.champions.map(_.cost).sum)).take(50),
                searchResultCount = state.searchResultCount + 1
              )
            }.runNow()
          }
          worker
        }
      }
      def setNewState(workers: Seq[Worker]) =
        $.setState(State.empty.copy(compositionConfig = newCompositionConfig, workers = workers))
      def startWorkers(workers: Seq[Worker]) =
        Callback(workers.foreach(_.postMessage(newCompositionConfig.asJson.noSpaces)))
      terminateWorkers >> (spawnWorkers >>= (w => setNewState(w) >> startWorkers(w)))
    }

    def render(state: State): VdomNode = {
      <.div(
        <.h1("TFT Team Composition Generator for Set 5.5"),
        CompositionForm(state.compositionConfig, handleCompositionConfigChange),
        CompositionResults(state.compositions, state.searchResultCount, state.compositionConfig),
        <.footer(
          "The TFT Team Composition Generator isn't endorsed by Riot Games and doesn't reflect the views or opinions of Riot Games or anyone officially involved in producing or managing Riot Games properties. Riot Games, and all associated properties are trademarks or registered trademarks of Riot Games, Inc.")
      )
    }
  }

  val Component =
    ScalaComponent
      .builder[Unit]("Composition Generator")
      .initialState(State.empty)
      .renderBackend[Backend]
      .componentDidMount(mounted => mounted.backend.handleCompositionConfigChange(mounted.state.compositionConfig))
      .build

  def apply(): Unmounted[Unit, State, Backend] = Component()
}
