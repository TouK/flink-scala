package org.apache.flink.table.api.runtime.types

import com.twitter.chill.{IKryoRegistrar, Kryo}

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
