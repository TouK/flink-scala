package org.apache.flink.api.java.typeutils.runtime.kryo

import org.apache.flink.api.common.serialization.SerializerConfigImpl
import org.apache.flink.table.api.runtime.types.FlinkScalaKryoInstantiator
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class KryoSerializerSpec extends AnyFlatSpec with Matchers {

  it should "now overwrite our type registrations" in {
    val ourKryo = new FlinkScalaKryoInstantiator().newKryo
    val flinkKryo = new KryoSerializer(classOf[TestClass], new SerializerConfigImpl()).getKryo

    // Index 85 is crucial as that's what's indicated as free by Flink's own ChillSerializerRegistrar
    for (id <- 0 until ourKryo.getNextRegistrationId) {
      val ourRegistration = ourKryo.getRegistration(id)
      withClue(ourRegistration) {
        ourRegistration.getType shouldBe flinkKryo.getRegistration(id).getType
      }
    }
  }

  class TestClass

}
