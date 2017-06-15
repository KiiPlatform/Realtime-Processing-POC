package com.kii.nre.spark
import com.kii.nre.util._
import com.kii.objects._
import org.apache.kafka.common.serialization.{StringDeserializer,StringSerializer}

object KafkaHelper {
    def generateParams(nreConf: NREConfig,nbDir: String) : Map[String, Object] = {
        Map[String, Object](
        "bootstrap.servers" -> nreConf.kafkaNREbootstrapServers,
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "partition.assignment.strategy" -> "org.apache.kafka.clients.consumer.RangeAssignor",
        "group.id" -> nreConf.kafkaNREGroupId,
        "auto.offset.reset" -> "latest",
        "enable.auto.commit" -> "false",
        "security.protocol" -> "SSL",
        "ssl.keystore.type" -> "JKS",
        "ssl.truststore.type" -> "JKS",
        "ssl.truststore.location" -> s"${nbDir}/truststore.jks",
        "ssl.truststore.password" -> nreConf.truststorePassword,
        "ssl.keystore.location" -> s"${nbDir}/keystore.jks",
        "ssl.keystore.password" -> nreConf.keystorePassword,
        "ssl.key.password" -> nreConf.keyPassword
        )
    }
}