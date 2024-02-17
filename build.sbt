ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val AkkaVersion = "2.9.1"


lazy val task1 = (project in file("send-request"))
  .settings(
    name := "send-request",
    resolvers += "Akka library repository".at("https://repo.akka.io/maven"),
    libraryDependencies ++= List(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "org.scalatest" %% "scalatest" % "3.2.18" % "test"
    )
  )

lazy val readData = (project in file("read-data"))
  .settings(
    name := "read-data",
    resolvers += "Akka library repository".at("https://repo.akka.io/maven"),
    libraryDependencies ++= List(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "org.scalatest" %% "scalatest" % "3.2.18" % "test"
    )
  )