ThisBuild / version := "1.0.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

name := "flink-scala-2.13"

lazy val flinkV = "1.16.1"


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
    assembly / assemblyJarName := "flink-scala-2.13",
    libraryDependencies ++= {
      Seq(
        "org.apache.flink" % "flink-streaming-java" % flinkV % "provided",
        "com.twitter" %% "chill" % "0.9.5" exclude("com.esotericsoftware", "kryo-shaded"),
        "com.esotericsoftware.kryo" % "kryo" % "2.24.0" % "provided",
      )
    }
  )
  .settings(publishSettings)

