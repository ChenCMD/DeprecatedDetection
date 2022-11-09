ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.8.0",
  "org.typelevel" %% "cats-effect" % "3.3.14",
  "tech.neander" %%% "langoustine-app" % "0.0.17",
  "org.scalatest" %% "scalatest" % "3.2.14" % Test
)

useYarn := true

lazy val root = (project in file("."))
  .settings(
    name := "IMPDocUtil",
    idePackagePrefix := Some("com.github.chencmd.impdocutil")
  )
