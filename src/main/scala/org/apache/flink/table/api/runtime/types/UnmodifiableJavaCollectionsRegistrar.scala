package org.apache.flink.table.api.runtime.types

import com.esotericsoftware.kryo.Kryo
import com.twitter.chill.java.{IterableRegistrar, UnmodifiableCollectionSerializer, UnmodifiableListSerializer, UnmodifiableMapSerializer, UnmodifiableSetSerializer, UnmodifiableSortedMapSerializer, UnmodifiableSortedSetSerializer}

/**
 * Registrations cherry-picked from [[com.twitter.chill.java.PackageRegistrar]]
 */
object UnmodifiableJavaCollectionsRegistrar {
  def apply(k: Kryo): Unit = new IterableRegistrar(
    UnmodifiableCollectionSerializer.registrar(),
    UnmodifiableListSerializer.registrar(),
    UnmodifiableMapSerializer.registrar(),
    UnmodifiableSetSerializer.registrar(),
    UnmodifiableSortedMapSerializer.registrar(),
    UnmodifiableSortedSetSerializer.registrar(),
  ).apply(k)
}
