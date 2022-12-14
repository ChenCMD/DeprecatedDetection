name := "Deprecated Detection"
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

enablePlugins(ScalaJSPlugin)

Compile / fastLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "dist"
Compile / fullLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "dist"

scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }

scalaJSUseMainModuleInitializer := true

javaOptions ++= Seq(
  "-Xmx2G",
  "-XX:+UseG1GC"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.3.14",
  "tech.neander" %%% "langoustine-app" % "0.0.17",
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  "net.exoego" %%% "scala-js-nodejs-v16" % "0.14.0" cross CrossVersion.for3Use2_13
)
