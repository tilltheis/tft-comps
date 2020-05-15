import sbtcrossproject.CrossPlugin.autoImport.crossProject
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.npmDependencies


lazy val tftcomps = project.in(file(".")).aggregate(cross.js, cross.jvm)

lazy val cross = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    name := "tft-comps",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.13.2",
    scalacOptions += "-deprecation",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.1" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % Test // intellij needs this to run shared tests
  )
  .jvmSettings(
    // Add JVM-specific settings here
  )
  .jsSettings(
    // Add JS-specific settings here
    scalaJSUseMainModuleInitializer := true,
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.6.0",
    npmDependencies in Compile ++= Seq("react" -> "16.13.1", "react-dom" -> "16.13.1"),
  )

lazy val js = cross.js.enablePlugins(ScalaJSBundlerPlugin)
lazy val jvm = cross.jvm
