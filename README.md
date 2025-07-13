# flink-scala

[![Maven Central Version](https://img.shields.io/maven-central/v/pl.touk/flink-scala_2.13)](https://central.sonatype.com/artifact/pl.touk/flink-scala_2.13/versions)
[![Docker Image Version](https://img.shields.io/docker/v/touk/flink?sort=date&label=Docker%20Hub)](https://hub.docker.com/r/touk/flink/tags)

This module is a replacement for the `org.apache.flink:flink-scala` lib shipped with flink distribution,
and allows using flink with higher scala versions than 2.12.8.

For more refer to <https://issues.apache.org/jira/browse/FLINK-13414>.

## Replacing flink-scala in flink distribution
```bash
rm $FLINK_HOME/lib/flink-scala*.jar

wget https://repo1.maven.org/maven2/pl/touk/flink-scala_2.12/1.1.4/flink-scala_2.12-1.1.4-assembly.jar -O $FLINK_HOME/lib/flink-scala_2.12-1.1.4-assembly.jar
```

## Using as a lib (probably only sufficient when child-first classloading is enabled on flink)
```scala
libraryDependencies += "pl.touk" %% "flink-scala" % "1.1.4"
```

## Prebuild flink images
* we provide prebuild flink docker images for scala 2.12 and 2.13 on [Docker Hub](https://hub.docker.com/r/touk/flink)

## Publishing (run on manual GitHub action)
```
sbt "+publishSigned; sonatypeBundleRelease"
```
