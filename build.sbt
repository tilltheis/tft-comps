import sbtcrossproject.CrossPlugin.autoImport.crossProject
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.npmDependencies

lazy val root = project.in(file(".")).aggregate(tftcomps.js, tftcomps.jvm)

lazy val tftcomps = crossProject(JSPlatform, JVMPlatform)
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
    libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.7.0",
    npmDependencies in Compile ++= Seq("react" -> "16.13.1", "react-dom" -> "16.13.1"),
  )

// this won't be used directly but this ensures that only the JS part and not the shared part is actually run via node
lazy val js = tftcomps.js.enablePlugins(ScalaJSBundlerPlugin)
