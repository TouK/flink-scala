package pl.touk.nussknacker

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FlinkScalaBuildInfoSpec  extends AnyFlatSpec with Matchers {
  it should "generate BuildInfo expected by Nussknacker" in {
    noException should be thrownBy {
      Class.forName("pl.touk.nussknacker.FlinkScalaBuildInfo")
    }
  }

  it should "write valid data into FlinkScalaBuildInfo" in {
    val buildInfo = FlinkScalaBuildInfo
    buildInfo.name shouldBe "flink-scala"
    buildInfo.version should startWith regex "\\d"
    buildInfo.flinkVersion should startWith regex "\\d"
  }
}
