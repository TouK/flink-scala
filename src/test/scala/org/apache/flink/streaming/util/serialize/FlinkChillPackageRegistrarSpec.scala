package org.apache.flink.streaming.util.serialize

import org.apache.flink.table.api.runtime.types.FlinkScalaKryoInstantiator
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FlinkChillPackageRegistrarSpec extends AnyFlatSpec with Matchers {

  it should "return valid next registration id" in {
    val ourKryo = new FlinkScalaKryoInstantiator().newKryo
    new FlinkChillPackageRegistrar().getNextRegistrationId shouldBe >= (ourKryo.getNextRegistrationId)
  }

}
