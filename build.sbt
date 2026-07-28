import sbtassembly.MergeStrategy

name := "flink-scala"

val scala213 = "2.13.18"

scalaVersion := scala213
crossScalaVersions := List(scala213)

val flinkV = settingKey[String]("Flink version") // to extract using `show flinkV`
flinkV := "2.3.0"
val flinkKryoV = "5.6.2"
val chillV = "1.0.0"
val scalaTestV = "3.2.19"

lazy val buildInfoSettings = Seq(
  buildInfoKeys    := Seq[BuildInfoKey](name, version),
  buildInfoKeys ++= Seq[BuildInfoKey](
    "flinkVersion" -> flinkV.value,
    "kryoVersion"  -> flinkKryoV,
    "chillVersion" -> chillV,
  ),
  buildInfoPackage := "pl.touk.nussknacker",
  buildInfoObject  := "FlinkScalaBuildInfo",
  buildInfoOptions ++= Seq(BuildInfoOption.ToMap),
)

lazy val assemblySettings = Seq(
  // exclude all provided dependencies from assembly
  assembly / fullClasspath := {
    val cp                   = (assembly / fullClasspath).value
    val providedDependencies = update.map(f => f.select(configurationFilter("provided"))).value
    cp filter { f => !providedDependencies.contains(f.data) }
  },
  assembly / artifact := {
    val art = (assembly / artifact).value
    art.withClassifier(Some("assembly"))
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
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "flink-scala",
    organization := "pl.touk",
    organizationName := "Nussknacker",
    organizationHomepage := Some(url("https://nussknacker.io")),
    licenses := List(License.Apache2),
    homepage := Some(url("https://github.com/TouK/flink-scala")),
    scalacOptions := Seq(
      "-encoding",
      "utf8",
      "-release",
      "11",
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-library" % scalaVersion.value,
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.apache.flink" % "flink-streaming-java" % flinkV.value % Provided,
      "pl.touk" %% "chill" % chillV,
      "com.esotericsoftware" % "kryo" % flinkKryoV % Provided,
      "org.scalatest" %% "scalatest" % scalaTestV % Test,
    ),
    resolvers ++= Seq(Resolver.sonatypeCentralSnapshots),
  )
  .settings(buildInfoSettings *)
  .settings(assemblySettings *)
  .settings(publishSettings)
