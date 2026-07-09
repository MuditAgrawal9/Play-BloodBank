name := """bloodbank"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.13.18"

libraryDependencies ++= Seq(
  guice,
  "com.mysql" % "mysql-connector-j" % "8.4.0",
  "org.mindrot" % "jbcrypt" % "0.4"
)

libraryDependencies ++= Seq(
  "io.jsonwebtoken" % "jjwt-api" % "0.11.5",
  "io.jsonwebtoken" % "jjwt-impl" % "0.11.5",
  "io.jsonwebtoken" % "jjwt-jackson" % "0.11.5"
)

libraryDependencies ++= Seq(
  "io.opentelemetry" % "opentelemetry-api" % "1.40.0",
  "io.opentelemetry" % "opentelemetry-sdk" % "1.40.0",
  "io.opentelemetry" % "opentelemetry-exporter-otlp" % "1.40.0"
)