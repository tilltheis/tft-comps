import sbtcrossproject.CrossPlugin.autoImport.crossProject
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.npmDependencies

ThisBuild / version := "0.1-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.2"
ThisBuild / scalacOptions += "-deprecation"

lazy val root = project.in(file(".")).aggregate(domain.jvm, domain.js, application, webworker)

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core",
    "io.circe" %%% "circe-generic",
    "io.circe" %%% "circe-parser"
  ).map(_ % "0.13.0")
)

lazy val domain = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("domain"))
  .settings(
    commonSettings,
    name := "tft-comps-domain",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.1" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % Test // intellij needs this to run shared tests
  )

lazy val application = project
  .in(file("application"))
  .dependsOn(domain.js)
  .settings(
    commonSettings,
    name := "tft-comps-application",
    scalaJSUseMainModuleInitializer := true,
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.7.0",
    npmDependencies in Compile ++= Seq("react" -> "16.13.1", "react-dom" -> "16.13.1")
  )
  .enablePlugins(ScalaJSBundlerPlugin)

lazy val webworker = project
  .in(file("webworker"))
  .dependsOn(domain.js)
  .settings(
    commonSettings,
    name := "tft-comps-webworker",
    scalaJSUseMainModuleInitializer := true
  )
  .enablePlugins(ScalaJSPlugin)
