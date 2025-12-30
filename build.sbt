import sbtassembly.MergeStrategy

name := "flink-scala"
version := "1.1.6"

val scala213 = "2.13.16"

scalaVersion := scala213
crossScalaVersions := List(scala213)

val flinkV = settingKey[String]("Flink version") // to extract using `show flinkV`
flinkV := "2.2.0"
val kryoV = "5.6.2"

lazy val scalaTestV = "3.2.19"

lazy val assemblySettings = Seq(
  assembly / artifact := {
    val art = (assembly / artifact).value
    art.withClassifier(Some("assembly"))
  },
  assembly / assemblyMergeStrategy := {
    case PathList(ps@_*) if ps.last == "module-info.class" => MergeStrategy.discard
    case x => MergeStrategy.defaultMergeStrategy(x)
  },
  addArtifact(assembly / artifact, assembly)
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  sonatypeCredentialHost := "central.sonatype.com",
  publishTo := {
    if (isSnapshot.value)
      Some("snapshots" at "https://central.sonatype.com/repository/maven-snapshots/")
    else {
      sonatypePublishToBundle.value
    }
  },
  Test / publishArtifact := false,
  //We don't put scm information here, it will be added by release plugin and if scm provided here is different than the one from scm
  //we'll end up with two scm sections and invalid pom...
  pomExtra in Global := {
    <scm>
      <connection>scm:git:github.com/TouK/flink-scala.git</connection>
      <developerConnection>scm:git:git@github.com:TouK/flink-scala.git</developerConnection>
      <url>github.com/TouK/flink-scala</url>
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
    name := "flink-scala",
    organization := "pl.touk",
    licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage := Some(url("https://github.com/TouK/flink-scala")),
    libraryDependencies ++= Seq(
      "org.apache.flink" % "flink-streaming-java" % flinkV.value % "provided",
      "com.xebialabs.chill" %% "chill" % "0.11.1",
      "com.esotericsoftware" % "kryo" % kryoV % "provided",
      "org.scala-lang" % "scala-library" % scalaVersion.value,
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalatest" %% "scalatest" % scalaTestV % Test,
    ),
    resolvers ++= Seq(
      "xebialabs" at "https://nexus.xebialabs.com/nexus/content/repositories/releases",
    ),
  )
  .settings(assemblySettings *)
  .settings(publishSettings)
