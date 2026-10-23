# flink-scala

[![Maven Central Version](https://img.shields.io/maven-central/v/pl.touk/flink-scala_2.13)](https://central.sonatype.com/artifact/pl.touk/flink-scala_2.13/versions)
[![Docker Image Version](https://img.shields.io/docker/v/touk/flink?sort=date&label=Docker%20Hub)](https://hub.docker.com/r/touk/flink/tags)

This module is a replacement for the `flink-scala_2.12-*.jar` shipped with the Flink distribution's `lib/` directory,
created to provide a shared Scala 2.13 library and to allow Scala 2.13 classes to be used in Flink's Kryo serializer.

For more details, see <https://issues.apache.org/jira/browse/FLINK-13414>.

Usage notes:

* serializers for Scala 2.13 classes can't be registered with `SerializerConfig` because that configuration
  is serialized using Java serialization and Flink SQL will fail to load it - `flink-table-planner-loader` bundles
  its own copy of Scala 2.12, which causes a `serialVersionUID` mismatch, e.g. for the `Range` class
* we recommend using Java types in Flink state and avoiding Scala types in Flink jobs

## Replacing `flink-scala` in the Flink distribution

```bash
rm $FLINK_HOME/lib/flink-scala*.jar
wget https://repo1.maven.org/maven2/pl/touk/flink-scala_2.13/2.0.2/flink-scala_2.13-2.0.2-assembly.jar -O $FLINK_HOME/lib/flink-scala_2.13-2.0.2-assembly.jar
```

## Pre-built Flink images

We provide pre-built Docker images for Flink with Scala 2.13 on [Docker Hub](https://hub.docker.com/r/touk/flink).

## Publishing

1. Change version in _version.sbt_ and this README file, commit changes
2. Add version tag (`git tag v...`)
3. Push changes and the tag to GitHub
4. Manually run the [_Publish_ workflow in GitHub Actions](https://github.com/TouK/flink-scala/actions/workflows/publish.yml)
5. Change version to a `-SNAPSHOT` version in _version.sbt_
6. Commit and push changes
