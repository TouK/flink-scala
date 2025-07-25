/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.types

import com.twitter.chill._
import org.apache.flink.api.java.typeutils.runtime.kryo.FlinkChillPackageRegistrar

import _root_.java.io.Serializable
import scala.collection.immutable.{ArraySeq, BitSet, HashMap, HashSet, ListMap, ListSet, NumericRange, Queue, Range, SortedMap, SortedSet}
import scala.collection.mutable.{Buffer, ListBuffer, ArraySeq => MArraySeq, BitSet => MBitSet, HashMap => MHashMap, HashSet => MHashSet, Map => MMap, Queue => MQueue, Set => MSet}
import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag
import scala.util.matching.Regex

/*
This code is copied as is from Twitter Chill 0.7.4 because we need to user a newer chill version
but want to ensure that the serializers that are registered by default stay the same.
The only changes to the code are those that are required to make it compile and pass checkstyle
checks in our code base.
 */

/**
 * This class has a no-arg constructor, suitable for use with reflection instantiation It has no
 * registered serializers, just the standard Kryo configured for Kryo.
 */
class EmptyFlinkScalaKryoInstantiator extends KryoInstantiator {
  override def newKryo = {
    val k = new KryoBase
    k.setRegistrationRequired(false)
    k.setInstantiatorStrategy(new org.objenesis.strategy.StdInstantiatorStrategy)

    // Handle cases where we may have an odd classloader setup like with libjars
    // for hadoop
    val classLoader = Thread.currentThread.getContextClassLoader
    k.setClassLoader(classLoader)

    k
  }
}

object FlinkScalaKryoInstantiator extends Serializable {
  private val mutex = new AnyRef with Serializable // some serializable object
  @transient private var kpool: KryoPool = null

  /** Return a KryoPool that uses the FlinkScalaKryoInstantiator */
  def defaultPool: KryoPool = mutex.synchronized {
    if (null == kpool) {
      kpool = KryoPool.withByteArrayOutputStream(guessThreads, new FlinkScalaKryoInstantiator)
    }
    kpool
  }

  private def guessThreads: Int = {
    val cores = Runtime.getRuntime.availableProcessors
    val GUESS_THREADS_PER_CORE = 4
    GUESS_THREADS_PER_CORE * cores
  }
}

/** Makes an empty instantiator then registers everything */
class FlinkScalaKryoInstantiator extends EmptyFlinkScalaKryoInstantiator {
  override def newKryo = {
    val k = super.newKryo
    val reg = new AllScalaRegistrar
    reg(k)
    k
  }
}

class ScalaCollectionsRegistrar extends IKryoRegistrar {
  def apply(newK: Kryo): Unit = {
    // for binary compat this is here, but could be moved to RichKryo
    def useField[T](cls: Class[T]): Unit = {
      val fs = new com.esotericsoftware.kryo.serializers.FieldSerializer(newK, cls)
      fs.setIgnoreSyntheticFields(false) // scala generates a lot of these attributes
      newK.register(cls, fs)
    }
    // The wrappers are private classes:
    useField(List(1, 2, 3).asJava.getClass)
    useField(List(1, 2, 3).iterator.asJava.getClass)
    useField(Map(1 -> 2, 4 -> 3).asJava.getClass)
    useField(new _root_.java.util.ArrayList().asScala.getClass)
    useField(new _root_.java.util.HashMap().asScala.getClass)

    /*
     * Note that subclass-based use: addDefaultSerializers, else: register
     * You should go from MOST specific, to least to specific when using
     * default serializers. The FIRST one found is the one used
     */
    newK
      .forTraversableSubclass(MArraySeq.empty[Any], isImmutable = false)
      .forSubclass[BitSet](new BitSetSerializer)
      .forSubclass[SortedSet[Any]](new SortedSetSerializer)
      .forClass[Some[Any]](new SomeSerializer[Any])
      .forClass[Left[Any, Any]](new LeftSerializer[Any, Any])
      .forClass[Right[Any, Any]](new RightSerializer[Any, Any])
      .forTraversableSubclass(Queue.empty[Any])
      // List is a sealed class, so there are only two subclasses:
      .forTraversableSubclass(List.empty[Any])
      // Add ListBuffer subclass before Buffer to prevent the more general case taking precedence
      .forTraversableSubclass(ListBuffer.empty[Any], isImmutable = false)
      // add mutable Buffer before Vector, otherwise Vector is used
      .forTraversableSubclass(Buffer.empty[Any], isImmutable = false)
      // Vector is a final class
      .forTraversableClass(Vector.empty[Any])
      .forTraversableSubclass(ListSet.empty[Any])
      // specifically register small sets since Scala represents them differently
      .forConcreteTraversableClass(Set[Any](Symbol("a")))
      .forConcreteTraversableClass(Set[Any](Symbol("a"), Symbol("b")))
      .forConcreteTraversableClass(Set[Any](Symbol("a"), Symbol("b"), Symbol("c")))
      .forConcreteTraversableClass(Set[Any](Symbol("a"), Symbol("b"), Symbol("c"), Symbol("d")))
      // default set implementation
      .forConcreteTraversableClass(HashSet[Any](Symbol("a"), Symbol("b"), Symbol("c"), Symbol("d"), Symbol("e")))
      // specifically register small maps since Scala represents them differently
      .forConcreteTraversableClass(Map[Any, Any](Symbol("a") -> Symbol("a")))
      .forConcreteTraversableClass(Map[Any, Any](Symbol("a") -> Symbol("a"), Symbol("b") -> Symbol("b")))
      .forConcreteTraversableClass(Map[Any, Any](Symbol("a") -> Symbol("a"), Symbol("b") -> Symbol("b"), Symbol("c") -> Symbol("c")))
      .forConcreteTraversableClass(Map[Any, Any](Symbol("a") -> Symbol("a"), Symbol("b") -> Symbol("b"), Symbol("c") -> Symbol("c"), Symbol("d") -> Symbol("d")))
      // default map implementation
      .forConcreteTraversableClass(
        HashMap[Any, Any](Symbol("a") -> Symbol("a"), Symbol("b") -> Symbol("b"), Symbol("c") -> Symbol("c"), Symbol("d") -> Symbol("d"), Symbol("e") -> Symbol("e")))
      // The normal fields serializer works for ranges
      .registerClasses(Seq(
        classOf[Range.Inclusive],
        classOf[NumericRange.Inclusive[_]],
        classOf[NumericRange.Exclusive[_]]))
      // Add some maps
      .forSubclass[SortedMap[Any, Any]](new SortedMapSerializer)
      .forTraversableSubclass(ListMap.empty[Any, Any])
      .forTraversableSubclass(HashMap.empty[Any, Any])
      // The above ListMap/HashMap must appear before this:
      .forTraversableSubclass(Map.empty[Any, Any])
      // here are the mutable ones:
      .forTraversableClass(MBitSet.empty, isImmutable = false)
      .forTraversableClass(MHashMap.empty[Any, Any], isImmutable = false)
      .forTraversableClass(MHashSet.empty[Any], isImmutable = false)
      .forTraversableSubclass(MQueue.empty[Any], isImmutable = false)
      .forTraversableSubclass(MMap.empty[Any, Any], isImmutable = false)
      .forTraversableSubclass(MSet.empty[Any], isImmutable = false)
  }
}

// In Scala 2.13 all java collections class wrappers were rewritten from case class to regular class. Now kryo does not
// serialize them properly, so this class was added to fix this issue. It might not be needed in the future, when flink
// or twitter-chill updates kryo.
class JavaWrapperScala2_13Registrar extends IKryoRegistrar {
  def apply(newK: Kryo): Unit = {
    newK.register(JavaWrapperScala2_13Serializers.mapSerializer.wrapperClass, JavaWrapperScala2_13Serializers.mapSerializer)
    newK.register(JavaWrapperScala2_13Serializers.setSerializer.wrapperClass, JavaWrapperScala2_13Serializers.setSerializer)
    newK.register(JavaWrapperScala2_13Serializers.listSerializer.wrapperClass, JavaWrapperScala2_13Serializers.listSerializer)
  }
}

/** Registers all the scala (and java) serializers we have */
class AllScalaRegistrar extends IKryoRegistrar {
  def apply(k: Kryo): Unit = {
    val col = new ScalaCollectionsRegistrar
    col(k)

    val jcol = new JavaWrapperCollectionRegistrar
    jcol(k)

    val jmap = new JavaWrapperScala2_13Registrar
    jmap(k)

    val smap = new ScalaCollectionsRegistrarCompat
    smap(k)

    // Register all 22 tuple serializers and specialized serializers
    ScalaTupleSerialization.register(k)
    k.forClass[Symbol](new KSerializer[Symbol] {
      override def isImmutable = true
      def write(k: Kryo, out: Output, obj: Symbol): Unit = { out.writeString(obj.name) }
      def read(k: Kryo, in: Input, cls: Class[Symbol]) = Symbol(in.readString)
    }).forSubclass[Regex](new RegexSerializer)
      .forClass[ClassTag[Any]](new ClassTagSerializer[Any])
      .forSubclass[Manifest[Any]](new ManifestSerializer[Any])
      .forSubclass[scala.Enumeration#Value](new EnumerationSerializer)

    // use the singleton serializer for boxed Unit
    val boxedUnit = scala.runtime.BoxedUnit.UNIT
    k.register(boxedUnit.getClass, new SingletonSerializer(boxedUnit))
    new FlinkChillPackageRegistrar().registerSerializers(k)
  }
}

/**
 * Scala collections registrar for compatibility between 2.12- and 2.13+.
 *
 * For 2.12- there's no extra classes that need to be registered.
 * @see [[ScalaCollectionsRegistrar]] and [[AllScalaRegistrar]] for all the
 * provided registrations.
 */
class ScalaCollectionsRegistrarCompat extends IKryoRegistrar {
  override def apply(newK: Kryo): Unit = {
    newK.register(classOf[Range.Exclusive])
    newK.forConcreteTraversableClass(Vector[Any]())
      .forConcreteTraversableClass(Vector(Symbol("a")))
    val t: TraversableSerializer[Any, Vector[_]] = new TraversableSerializer(true)
    newK.register(Class.forName("scala.collection.immutable.Vector0$"), t)
    newK.register(Class.forName("scala.collection.immutable.Vector1"), t)
    newK.register(Class.forName("scala.collection.immutable.Vector2"), t)
    newK.register(Class.forName("scala.collection.immutable.Vector3"), t)
    newK.register(Class.forName("scala.collection.immutable.Vector4"), t)
    newK.register(Class.forName("scala.collection.immutable.Vector5"), t)
    newK.register(Class.forName("scala.collection.immutable.Vector6"), t)
    newK.registerClasses(
      Seq(
        ArraySeq.unsafeWrapArray(Array[Byte]()).getClass,
        ArraySeq.unsafeWrapArray(Array[Short]()).getClass,
        ArraySeq.unsafeWrapArray(Array[Int]()).getClass,
        ArraySeq.unsafeWrapArray(Array[Long]()).getClass,
        ArraySeq.unsafeWrapArray(Array[Float]()).getClass,
        ArraySeq.unsafeWrapArray(Array[Double]()).getClass,
        ArraySeq.unsafeWrapArray(Array[Boolean]()).getClass,
        ArraySeq.unsafeWrapArray(Array[Char]()).getClass,
        ArraySeq.unsafeWrapArray(Array[String]()).getClass
      )
    )
  }
}
