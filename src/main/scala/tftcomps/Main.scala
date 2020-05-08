package tftcomps

import org.scalajs.dom.document
import tftcomps.application.CompositionGenerator

object Main extends App {
  CompositionGenerator().renderIntoDOM(document.getElementById("app"))
}
