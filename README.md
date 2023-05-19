# flink-scala-2.13

For now Flink does not support Scala 2.13. For more refer to <https://issues.apache.org/jira/browse/FLINK-13414>.

Our solution to deploy Scala 2.13 code to Flink, until it's officially supported (or Flink becomes really scala-free):

```bash
rm $FLINK_HOME/lib/flink-scala*.jar
wget https://repo1.maven.org/maven2/pl/touk/flink-scala-2-13_2.13/1.1.0/flink-scala-2-13_2.13-1.1.0-assembly.jar -O $FLINK_HOME/lib/flink-scala-2-13_2.13-1.1.0-assembly.jar
```

## Publishing
```
sbt publishSigned sonatypeBundleRelease
```
