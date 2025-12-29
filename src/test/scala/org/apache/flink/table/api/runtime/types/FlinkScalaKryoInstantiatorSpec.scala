package org.apache.flink.table.api.runtime.types

import com.esotericsoftware.kryo.io.{Input, Output}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.Collections
import scala.collection.mutable
import scala.jdk.CollectionConverters._

class FlinkScalaKryoInstantiatorSpec extends AnyFlatSpec with Matchers {

  private val kryo = new FlinkScalaKryoInstantiator().newKryo

  it should "serialize and deserialize records properly" in {
    val record = Record(true, 5, "abc",
      Map("a" -> 1, "b" -> 2), List("123", "abc"), Set("abc"),
      mutable.Map("a" -> 123).asJava, mutable.Buffer("abc").asJava, mutable.Set("abc").asJava)
    checkSerializeDeserializeRoundTrip(record)
  }
  it should "serialize and deserialize unmodifiableList" in {
    val obj = Collections.unmodifiableList(List("foo", "bar").asJava)
    checkSerializeDeserializeRoundTrip(obj)
  }

  it should "serialize and deserialize unmodifiableMap" in {
    val obj = Collections.unmodifiableMap(Map("foo" -> 1, "bar" -> 2).asJava)
    checkSerializeDeserializeRoundTrip(obj)
  }

  private def checkSerializeDeserializeRoundTrip(obj: Any) = {
    val output = new Output(1024)
    kryo.writeClassAndObject(output, obj)
    val input = new Input(output.toBytes)
    val result = kryo.readClassAndObject(input)

    result shouldBe obj
  }
}

case class Record(boolean: Boolean, int: Int, string: String,
                  map: Map[String, Int], list: List[String], set: Set[String],
                  javaMap: java.util.Map[String, Int], javaList: java.util.List[String], javaSet: java.util.Set[String])
