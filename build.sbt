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