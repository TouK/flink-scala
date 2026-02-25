package org.apache.flink.api.java.typeutils.runtime.kryo

import com.esotericsoftware.kryo.Kryo
import com.twitter.chill.java.{ArraysAsListSerializer, BitSetSerializer, InetSocketAddressSerializer, LocaleSerializer, RegexSerializer, SimpleDateFormatSerializer, SqlDateSerializer, SqlTimeSerializer, TimestampSerializer, URISerializer, UUIDSerializer}

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
 * We need this because our [[org.apache.flink.runtime.types.FlinkScalaKryoInstantiator]]
 * declares more serializers than the original Chill library, and the hardcoded free id of '85' is too low.
 *
 * We're breaking compatibility anyway, so we also drop original registration ids.
 */
class FlinkChillPackageRegistrar extends ChillSerializerRegistrar {

  override def registerSerializers(k: Kryo): Unit = {
    k.register(_root_.java.util.Arrays.asList("").getClass, new ArraysAsListSerializer())
    k.register(classOf[util.BitSet], new BitSetSerializer())
    k.register(classOf[PriorityQueue[_]], new PriorityQueueSerializer())
    k.register(classOf[Pattern], new RegexSerializer())
    k.register(classOf[Date], new SqlDateSerializer())
    k.register(classOf[Time], new SqlTimeSerializer())
    k.register(classOf[Timestamp], new TimestampSerializer())
    k.register(classOf[URI], new URISerializer())
    k.register(classOf[InetSocketAddress], new InetSocketAddressSerializer())
    k.register(classOf[UUID], new UUIDSerializer())
    k.register(classOf[Locale], new LocaleSerializer())
    k.register(classOf[SimpleDateFormat], new SimpleDateFormatSerializer())
  }

  // at the moment this was written our Kryo.getNextRegistrationId was returning 112
  override def getNextRegistrationId: Int = 150

}
