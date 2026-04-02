# flink-scala

[![Maven Central Version](https://img.shields.io/maven-central/v/pl.touk/flink-scala_2.13)](https://central.sonatype.com/artifact/pl.touk/flink-scala_2.13/versions)
[![Docker Image Version](https://img.shields.io/docker/v/touk/flink?sort=date&label=Docker%20Hub)](https://hub.docker.com/r/touk/flink/tags)

This module is a replacement for the `org.apache.flink:flink-scala` lib shipped with flink distribution,
and allows using Flink with Scala 2.13.

For more refer to <https://issues.apache.org/jira/browse/FLINK-13414>.

## Replacing flink-scala in flink distribution

```bash
rm $FLINK_HOME/lib/flink-scala*.jar

wget https://repo1.maven.org/maven2/pl/touk/flink-scala_2.13/2.0.0/flink-scala_2.13-2.0.0-assembly.jar -O $FLINK_HOME/lib/flink-scala_2.13-2.0.0-assembly.jar
```

## Using as a lib (probably only sufficient when child-first classloading is enabled on flink)

```scala
libraryDependencies += "pl.touk" %% "flink-scala" % "2.0.0"
```

## Pre-built Flink images

We provide pre-built Docker images for Flink with Scala 2.13 on [Docker Hub](https://hub.docker.com/r/touk/flink).

## Publishing

1. Change version in _version.sbt_, commit changes
2. Add version tag (`git tag v...`)
3. Push changes to GitHub 
4. Manually run the [_Publish_ workflow in GitHub Actions](https://github.com/TouK/flink-scala/actions/workflows/publish.yml)
5. Change version to a `-SNAPSHOT` version in version.sbt
6. Commit and push changes
