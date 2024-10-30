version := "1.1.2"

scalaVersion := "2.13.15"

name := "flink-scala-2.13"

lazy val flinkV = "1.16.2"
lazy val scalaTestV = "3.2.17"


assembly / artifact := {
  val art = (assembly / artifact).value
  art.withClassifier(Some("assembly"))
}

addArtifact(assembly / artifact, assembly)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
      val defaultNexusUrl = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at defaultNexusUrl + "content/repositories/snapshots")
      else {
        sonatypePublishToBundle.value
      }
  },
  Test / publishArtifact := false,
  //We don't put scm information here, it will be added by release plugin and if scm provided here is different than the one from scm
  //we'll end up with two scm sections and invalid pom...
  pomExtra in Global := {
    <scm>
      <connection>scm:git:github.com/TouK/flink-scala-2.13.git</connection>
      <developerConnection>scm:git:git@github.com:TouK/flink-scala-2.13.git</developerConnection>
      <url>github.com/TouK/flink-scala-2.13</url>
    </scm>
    <developers>
      <developer>
        <id>TouK</id>
        <name>TouK</name>
        <url>https://touk.pl</url>
      </developer>
    </developers>
  },
  organization := "pl.touk",
)

lazy val root = (project in file("."))
  .settings(
    name := "flink-scala-2.13",
    organization := "pl.touk",
    licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage := Some(url("https://github.com/TouK/flink-scala-2.13")),
    libraryDependencies ++= {
      Seq(
        "org.scala-lang" % "scala-compiler" % scalaVersion.value,
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,

        "org.apache.flink" % "flink-streaming-java" % flinkV % "provided",
        "com.twitter" %% "chill" % "0.9.5" exclude("com.esotericsoftware", "kryo-shaded"),
        "com.esotericsoftware.kryo" % "kryo" % "2.24.0" % "provided",

        "org.scalatest" %% "scalatest" % scalaTestV % "test",
      )
    }
  )
  .settings(publishSettings)
