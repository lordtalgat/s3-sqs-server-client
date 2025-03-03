ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

val http4sVersion = "1.0-234-d1a2b53"
val httpVer = "0.21.34"
val awsVersion = "2.20.0"
val ioCirceVersion = "0.14.10"

lazy val root = (project in file("."))
  .settings(
    name := "s3-sqs-server-client"
  )

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % httpVer,  // HTTP4s server (Blaze)
  "org.http4s" %% "http4s-dsl"         % httpVer,  // HTTP4s DSL for routing
  "org.http4s" %% "http4s-ember-server" % httpVer,  // Ember server (ensure Ember is included)
  "org.typelevel" %% "cats-effect"     % "2.5.1",     // Cats-Effect for asynchronous programming
  "software.amazon.awssdk" % "s3" % awsVersion,
  "software.amazon.awssdk" % "sqs" % awsVersion,
  "io.circe" %% "circe-core" % ioCirceVersion,
  "io.circe" %% "circe-generic" % ioCirceVersion,
  "io.circe" %% "circe-parser" % ioCirceVersion
)