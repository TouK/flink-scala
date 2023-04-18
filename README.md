# flink-scala-2.13

For now Flink does not support scala 2.13. For more refer to https://issues.apache.org/jira/browse/FLINK-13414

Our solution to deploy scala 2.13 code to Flink, until it's officially supported:

```bash
rm $FLINK_HOME/lib/flink-scala*.jar
wget https://repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.10/scala-library-2.13.10.jar -O $FLINK_HOME/lib/scala-library-2.13.10.jar
wget https://repo1.maven.org/maven2/org/scala-lang/scala-reflect/2.13.10/scala-reflect-2.13.10.jar -O $FLINK_HOME/lib/scala-reflect-2.13.10.jar
wget https://repo1.maven.org/maven2/pl/touk/flink-scala-2-13_2.13/1.0.0/flink-scala-2-13_2.13-1.0.0-assembly.jar -O $FLINK_HOME/lib/flink-scala-2-13_2.13-1.0.0-assembly.jar
```

This repo contains sources of the last jar.
