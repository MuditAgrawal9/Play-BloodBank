// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.11")

//Ebean plugin
//addSbtPlugin("org.playframework" % "sbt-play-ebean" % "7.5.0")
addSbtPlugin("com.typesafe.play" % "sbt-play-ebean" % "7.5.0")

// Defines scaffolding (found under .g8 folder)
// http://www.foundweekends.org/giter8/scaffolding.html
// sbt "g8Scaffold form"
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.18.0")
