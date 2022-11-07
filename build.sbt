ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

enablePlugins(ScalaJSPlugin)
enablePlugins(ScalablyTypedConverterPlugin)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.8.0",
  "org.typelevel" %% "cats-effect" % "3.3.14",
  "org.scalatest" %% "scalatest" % "3.2.12" % Test
)

useYarn := true
Compile / npmDependencies ++= Seq(
  "typescript-language-server" -> "2.1.0"
)

lazy val root = (project in file("."))
  .settings(
    name := "IMPDocUtil",
    idePackagePrefix := Some("com.github.chencmd.impdocutil")
  )
