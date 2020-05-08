enablePlugins(ScalaJSBundlerPlugin)

name := "tft-comps"

version := "0.1"

scalaVersion := "2.13.2"

scalaJSUseMainModuleInitializer := true

webpackBundlingMode := BundlingMode.LibraryOnly()

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.6.0"

npmDependencies in Compile ++= Seq("react" -> "16.13.1", "react-dom" -> "16.13.1")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test"
