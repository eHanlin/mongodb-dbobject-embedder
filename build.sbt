name := "mongodb-dbobject-embedder"

organization := "tw.com.ehanlin"

organizationHomepage := Some(new URL("http://www.ehanlin.com.tw"))

homepage := Some(url("https://github.com/eHanlin/sbt-script-args-parser"))

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

description := "Lets you use string easily embed other collection's DBObject"

version := "0.0.6"

scalaVersion := "2.11.6"

isSnapshot := false

publishMavenStyle := true

publishArtifact in Test := false

pomExtra := (
  <scm>
    <url>git@github.com:eHanlin/mongodb-dbobject-embedder.git</url>
    <connection>scm:git:git@github.com:eHanlin/mongodb-dbobject-embedder.git</connection>
  </scm>
    <developers>
      <developer>
        <id>DdGWRv8u</id>
        <name>hotdog929</name>
      </developer>
    </developers>)


bintrayReleaseOnPublish in ThisBuild := false


crossPaths := false

autoScalaLibrary := false

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.mongodb" % "mongo-java-driver" % "2.12.0" % Provided,
  "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.48.0" % Test,
  "org.specs2" %% "specs2-core" % "3.6.1" % Test
)

scalacOptions in Test ++= Seq("-Yrangepos")

parallelExecution in Test := false