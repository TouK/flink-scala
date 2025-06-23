#!/bin/bash
set -e
sbt -Dsbt.supershell=false "show version"
sbt -Dsbt.supershell=false "show version" | grep "info" | tail -1
sbt -Dsbt.supershell=false "show version" | grep "info" | tail -1 | awk '{print $2}'
VERSION=$(sbt -Dsbt.supershell=false "show version" | grep "info" | tail -1 | awk '{print $2}')
FLINK_VERSION=$(sbt -Dsbt.supershell=false "show flinkV" | grep "info" | tail -1 | awk '{print $2}')
echo "FLINK_SCALA_VERSION: ${VERSION}"
echo "FLINK_VERSION: ${FLINK_VERSION}"

if [[ "$1" == "--push" ]]; then
  OUTPUT_TYPE="registry"
else
  OUTPUT_TYPE="docker"
fi

sbt "++clean;++assembly"

IMAGE_TAG="${VERSION}-flink${FLINK_VERSION}-scala_2.12"
echo "Building Docker image with version: $IMAGE_TAG"

cp target/scala-2.12/flink-scala-assembly-${VERSION}.jar .

docker buildx build \
  --build-arg FLINK_VERSION=$FLINK_VERSION \
  --build-arg FLINK_SCALA_VERSION=$VERSION \
  --platform linux/amd64,linux/arm64 \
  --tag touk/flink:$IMAGE_TAG \
  --output=type=$OUTPUT_TYPE .

IMAGE_TAG="${VERSION}-flink${FLINK_VERSION}-scala_2.13"
echo "Building Docker image with version: $IMAGE_TAG"

cp target/scala-2.13/flink-scala-assembly-${VERSION}.jar .

docker buildx build \
  --build-arg FLINK_VERSION=$FLINK_VERSION \
  --build-arg FLINK_SCALA_VERSION=$VERSION \
  --platform linux/amd64,linux/arm64 \
  --tag touk/flink:$IMAGE_TAG \
  --output=type=$OUTPUT_TYPE .
