package tftcomps.application

import org.scalajs.dom.document

object Main extends App {
  CompositionGenerator().renderIntoDOM(document.getElementById("app"))
}
