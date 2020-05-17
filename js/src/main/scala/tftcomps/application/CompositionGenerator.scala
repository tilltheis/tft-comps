package tftcomps.application

import japgolly.scalajs.react.{Callback, CallbackTo, ScalaComponent}
import japgolly.scalajs.react.component.Scala.{BackendScope, Unmounted}
import japgolly.scalajs.react.vdom.html_<^._
import tftcomps.domain.{Composition, data, search2}

import scala.util.Random

object CompositionGenerator {
  final case class State(compositionConfig: CompositionConfig,
                         compositions: LazyList[(Composition, Double)],
                         compositionRenderLimit: Long)

  final case class Backend($ : BackendScope[Unit, State]) {
    private var compositionPullTimeoutHandler: Option[scalajs.js.timers.SetTimeoutHandle] = None

    def startPullingCompositions(compositions: LazyList[(Composition, Double)]): Callback = Callback {
      compositionPullTimeoutHandler.foreach(scalajs.js.timers.clearTimeout)
      compositionPullTimeoutHandler =
        Some(scalajs.js.timers.setTimeout(0)(pullNextComposition(compositions.iterator).runNow()))
    }

    private def pullNextComposition(iterator: Iterator[(Composition, Double)]): Callback = {
      val pullNext = CallbackTo(iterator.hasNext)
      val scheduleNextPull = CallbackTo {
        compositionPullTimeoutHandler = Some(scalajs.js.timers.setTimeout(1) {
          val _ = iterator.next()
          pullNextComposition(iterator).runNow()
        })
      }
      val updateState = $.modState(s => s.copy(compositionRenderLimit = s.compositionRenderLimit + 1))
      pullNext >>= (hasNext => if (hasNext) updateState >> scheduleNextPull else Callback.empty)
    }

    def findCompositions(compositionConfig: CompositionConfig): LazyList[(Composition, Double)] = {
      LazyList(0 until 500: _*).flatMap { _ =>
        // Shuffle champion list because the order of champions influences the result.
        // The search algorithm cannot possibly find the perfect solution w/o brute force.
        search2(
          Random.shuffle(data.champions.all.toSeq).filter(_.cost <= compositionConfig.maxChampionCost),
          compositionConfig.maxTeamSize,
          compositionConfig.requiredRoles,
          compositionConfig.requiredChampions
        ).take(1)
      }
    }

    def handleCompositionConfigChange(newCompositionConfig: CompositionConfig): Callback = {
      val newCompositions = findCompositions(newCompositionConfig)
//      val newCompositions = LazyList.empty
      val newState =
        State(compositionConfig = newCompositionConfig, compositions = newCompositions, compositionRenderLimit = 0)
      $.setState(newState) >> startPullingCompositions(newCompositions)
    }

    def render(state: State): VdomNode = {
      <.div(
        <.h1("TFT Team Composition Generator"),
        CompositionForm(state.compositionConfig, handleCompositionConfigChange),
        CompositionResults(state.compositions
          .take(state.compositionRenderLimit.toInt)
          .distinctBy(_._1.champions.map(_.name).mkString) // this shouldn't be necessary but `.toSet` yields duplicates
          .toSet)
      )
    }
  }

  val Component =
    ScalaComponent
      .builder[Unit]("Composition Generator")
      .initialState(State(
        CompositionConfig(maxTeamSize = 8,
                          maxChampionCost = 5,
                          requiredRoles = data.roles.all.map(_ -> 0).toMap,
                          requiredChampions = Set.empty),
        compositions = LazyList.empty,
        compositionRenderLimit = 0
      ))
      .renderBackend[Backend]
      .componentDidMount(mounted => mounted.backend.handleCompositionConfigChange(mounted.state.compositionConfig))
      .build

  def apply(): Unmounted[Unit, State, Backend] = Component()
}
