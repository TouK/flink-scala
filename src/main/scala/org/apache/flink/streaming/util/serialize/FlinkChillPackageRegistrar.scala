package org.apache.flink.streaming.util.serialize

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.serializers.{DefaultSerializers, JavaSerializer}
import org.apache.flink.api.java.typeutils.runtime.kryo.ChillSerializerRegistrar

import java.net.{InetSocketAddress, URI}
import java.sql.{Date, Time, Timestamp}
import java.text.SimpleDateFormat
import java.util
import java.util.regex.Pattern
import java.util.{Locale, PriorityQueue, UUID}

/**
 * Overwrites the original class so that we can provide higher free id
 * to [[org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer]].
 *
 * We need this because our [[org.apache.flink.table.api.runtime.types.FlinkScalaKryoInstantiator]]
 * declares more serializers than the original Chill library, and the hardcoded free id of '85' is too low.
 *
 * We're breaking compatibility anyway, so we also drop original registration ids.
 */
class FlinkChillPackageRegistrar extends ChillSerializerRegistrar {

  override def registerSerializers(k: Kryo): Unit = {
    k.register(_root_.java.util.Arrays.asList("").getClass, new DefaultSerializers.ArraysAsListSerializer())
    k.register(classOf[util.BitSet], new DefaultSerializers.BitSetSerializer())
    k.register(classOf[PriorityQueue[_]], new DefaultSerializers.PriorityQueueSerializer())
    k.register(classOf[Pattern], new DefaultSerializers.PatternSerializer())
    k.register(classOf[Date], new DefaultSerializers.DateSerializer())
    k.register(classOf[Time], new DefaultSerializers.DateSerializer())
    k.register(classOf[Timestamp], new DefaultSerializers.TimestampSerializer())
    k.register(classOf[URI], new DefaultSerializers.URISerializer())
    k.register(classOf[InetSocketAddress], new InetSocketAddressSerializer())
    k.register(classOf[UUID], new DefaultSerializers.UUIDSerializer())
    k.register(classOf[Locale], new DefaultSerializers.LocaleSerializer())
    k.register(classOf[SimpleDateFormat], new JavaSerializer())
  }

  // at the moment this was written our Kryo.getNextRegistrationId was returning 112
  override def getNextRegistrationId: Int = 150

}
