import sbtassembly.MergeStrategy

name := "flink-scala"
version := "1.1.4"

val scala212 = "2.12.20"
val scala213 = "2.13.16"

scalaVersion := scala212
crossScalaVersions := List(scala212, scala213)

val flinkV = settingKey[String]("Flink version") // to extract using `show flinkV`
flinkV := "1.20.2"

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
    libraryDependencies ++= (forScalaVersion(scalaVersion.value) {
      case (2, 12) =>
        Seq(
          "org.apache.flink" %% "flink-scala" % flinkV.value excludeAll(
            ExclusionRule(organization = "org.apache.flink", name = "flink-core"),
            ExclusionRule(organization = "org.apache.flink", name = "flink-java"),
            ExclusionRule(organization = "org.apache.flink", name = "flink-shaded-asm-9"),
            ExclusionRule(organization = "org.slf4j", name = "slf4j-api"),
            ExclusionRule(organization = "com.google.code.findbugs", name = "jsr305"),
          ),
          "com.esotericsoftware.kryo" % "kryo" % "2.24.0" % Test,
          "org.apache.flink" % "flink-java" % flinkV.value % Test,
        )
      case (2, 13) =>
        Seq(
          "org.apache.flink" % "flink-streaming-java" % flinkV.value % "provided",
          "com.twitter" %% "chill" % "0.9.5" exclude("com.esotericsoftware", "kryo-shaded"),
          "com.esotericsoftware.kryo" % "kryo" % "2.24.0" % "provided",
        )
    } ++ Seq(
      "org.scala-lang" % "scala-library" % scalaVersion.value,
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalatest" %% "scalatest" % scalaTestV % Test,
    ))
  )
  .settings(assemblySettings *)
  .settings(publishSettings)

def forScalaVersion[T](version: String)(provide: PartialFunction[(Int, Int), T]): T = {
  CrossVersion.partialVersion(version) match {
    case Some((major, minor)) if provide.isDefinedAt((major.toInt, minor.toInt)) =>
      provide((major.toInt, minor.toInt))
    case Some(_) =>
      throw new IllegalArgumentException(s"Scala version $version is not handled")
    case None =>
      throw new IllegalArgumentException(s"Invalid Scala version $version")
  }
}
