ARG FLINK_VERSION="invalid"

FROM flink:${FLINK_VERSION}-scala_2.12-java17

RUN rm $FLINK_HOME/lib/flink-scala*.jar

ARG FLINK_SCALA_VERSION="invalid"
COPY --chown=flink:flink flink-scala-assembly-${FLINK_SCALA_VERSION}.jar $FLINK_HOME/lib/
