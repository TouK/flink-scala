package org.apache.flink.api.java.typeutils.runtime.kryo

import org.apache.flink.runtime.types.FlinkScalaKryoInstantiator
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FlinkChillPackageRegistrarSpec extends AnyFlatSpec with Matchers {

  it should "return valid next registration id" in {
    val ourKryo = new FlinkScalaKryoInstantiator().newKryo
    new FlinkChillPackageRegistrar().getNextRegistrationId shouldBe >= (ourKryo.getNextRegistrationId)
  }

}
