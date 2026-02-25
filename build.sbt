import sbtassembly.MergeStrategy

name := "flink-scala"
version := "1.1.6"

val scala213 = "2.13.18"

scalaVersion := scala213
crossScalaVersions := List(scala213)

val flinkV = settingKey[String]("Flink version") // to extract using `show flinkV`
flinkV := "1.20.3"

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
  pomIncludeRepository := { _ => false },
  publishTo := {
    if (isSnapshot.value) Some("central-snapshots" at "https://central.sonatype.com/repository/maven-snapshots/")
    else localStaging.value
  },
  Test / publishArtifact := false,
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/TouK/flink-scala"),
      "scm:git@github.com:TouK/flink-scala.git"
    )
  ),
  pomExtra := List(
    <developers>
      <developer>
        <name>Nussknacker Team</name>
        <email>info@nussknacker.io</email>
        <organization>Nussknacker</organization>
        <organizationUrl>https://nussknacker.io</organizationUrl>
      </developer>
    </developers>
  ),
)

lazy val root = (project in file("."))
  .settings(
    name := "flink-scala",
    organization := "pl.touk",
    organizationName := "Nussknacker",
    organizationHomepage := Some(url("https://nussknacker.io")),
    licenses := List(License.Apache2),
    homepage := Some(url("https://github.com/TouK/flink-scala")),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-library" % scalaVersion.value,
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.twitter" %% "chill" % "0.9.5" exclude("com.esotericsoftware", "kryo-shaded"),
      "org.apache.flink" % "flink-streaming-java" % flinkV.value % Provided,
      "com.esotericsoftware.kryo" % "kryo" % "2.24.0" % Provided,
      "org.scalatest" %% "scalatest" % scalaTestV % Test,
    )
  )
  .settings(assemblySettings *)
  .settings(publishSettings)
