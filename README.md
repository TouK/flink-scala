# flink-scala

This module is a replacement for the `org.apache.flink:flink-scala` lib shipped with flink distribution,
and allows using flink with higher scala versions than 2.12.8.

For more refer to <https://issues.apache.org/jira/browse/FLINK-13414>.

## Replacing flink-scala in flink distribution
```bash
rm $FLINK_HOME/lib/flink-scala*.jar

wget https://central.sonatype.com/repository/maven-snapshots/pl/touk/flink-scala_2.13/1.1.3-SNAPSHOT/flink-scala_2.13-1.1.3-SNAPSHOT-assembly.jar -O $FLINK_HOME/lib/flink-scala_2.13-1.1.3-SNAPSHOT-assembly.jar
```

## Using as a lib (probably only sufficient when child-first classloading is enabled on flink)
```scala
libraryDependencies += "pl.touk" %% "flink-scala" % "1.1.3-SNAPSHOT"
```

## Prebuild flink images
* we provide prebuild flink docker images for scala 2.12 and 2.13 on [Docker Hub](https://hub.docker.com/r/touk/flink)

## Publishing
```
sbt publishSigned sonatypeBundleRelease
```
