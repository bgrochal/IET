name := "AkkaReactiveDemo"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor" % "2.4.11",

  // Tests
  "com.typesafe.akka" %% "akka-testkit" % "2.4.11" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",

  // Persistence
  "com.typesafe.akka" %% "akka-persistence" % "2.4.11",
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",

  // Remote communication
  "com.typesafe.akka" %% "akka-remote" % "2.4.11"
)
