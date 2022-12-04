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
  ).map(_ % "0.14.3")
)

lazy val domain = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("domain"))
  .settings(
    commonSettings,
    name := "tft-comps-domain",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.14" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test, // intellij needs this to run shared tests
    libraryDependencies += "org.scalatestplus" %% "scalacheck-1-17" % "3.2.14.0" % Test,
    libraryDependencies += "org.scalatestplus" %% "scalacheck-1-17" % "3.2.14.0" % Test
  )

lazy val copyAssetsToTargetDirectory =
  taskKey[Unit]("Copy assets from resources to target directory to have same paths for development and production")

lazy val application = project
  .in(file("application"))
  .dependsOn(domain.js)
  .settings(
    commonSettings,
    name := "tft-comps-application",
    scalaJSUseMainModuleInitializer := true,
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "2.1.1",
    Compile / npmDependencies ++= Seq("react" -> "17.0.2", "react-dom" -> "17.0.2"),
    Assets / sourceDirectory := file("application/src/main/resources"),
    copyAssetsToTargetDirectory := {
      println("Copying CSS assets to resources...")
      val source = file("application/src/main/resources")
      val target = file("application/target/web/sass/main")
      IO.copyDirectory(source, target)
    },
    Compile / compile := {
      val x = (Compile / compile).value
      copyAssetsToTargetDirectory.value
      x
    }
  )
  .enablePlugins(ScalaJSBundlerPlugin, SbtSassify)

lazy val webworker = project
  .in(file("webworker"))
  .dependsOn(domain.js)
  .settings(
    commonSettings,
    name := "tft-comps-webworker",
    scalaJSUseMainModuleInitializer := true
  )
  .enablePlugins(ScalaJSPlugin)
