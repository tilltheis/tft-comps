package tftcomps.application

import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, CallbackTo, ScalaComponent}
import org.scalajs.dom.raw.{MessageEvent, Worker}
import tftcomps.domain.{Composition, CompositionConfig, data}

object CompositionGenerator {
  final case class State(compositionConfig: CompositionConfig,
                         compositions: Seq[Composition],
                         searchResultCount: Int,
                         worker: Option[Worker])
  object State {
    val empty: State = State(
      CompositionConfig(maxTeamSize = 8,
                        maxChampionCost = 5,
                        requiredRoles = data.CurrentSet.roles.all.map(_ -> 0).toMap,
                        requiredChampions = Set.empty,
                        searchThoroughness = 2),
      Seq.empty,
      0,
      None
    )
  }

  final case class Backend($ : BackendScope[Unit, State]) {
    def handleCompositionConfigChange(newCompositionConfig: CompositionConfig): Callback = {
      import io.circe.parser._
      import io.circe.syntax._
      import tftcomps.domain.codec._

      val terminateWorker = $.state.map(_.worker.foreach(_.terminate()))
      val spawnWorker = CallbackTo {
        val worker = new Worker("../../../../webworker/target/scala-2.13/tft-comps-webworker-fastopt.js")
        worker.onmessage = { (event: MessageEvent) =>
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
      def setNewState(worker: Worker) =
        $.setState(State.empty.copy(compositionConfig = newCompositionConfig, worker = Some(worker)))
      def startWorker(worker: Worker) = Callback(worker.postMessage(newCompositionConfig.asJson.noSpaces))
      terminateWorker >> (spawnWorker >>= (w => setNewState(w) >> startWorker(w)))
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
