name := "mongodb-dbobject-embedder"

organization := "tw.com.ehanlin"

version := "0.0.1-SNAPSHOT"

publishMavenStyle := true

crossPaths := false

autoScalaLibrary := false

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.mongodb" % "mongo-java-driver" % "3.0.2",
  "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.47.3" % Test,
  "org.specs2" %% "specs2-core" % "3.6.1" % Test
)

scalacOptions in Test ++= Seq("-Yrangepos")