package com.kii.nre.spark

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.kafka.clients.consumer.RangeAssignor
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext, Duration, Seconds}


object KiiNREBridge {
  def main(args: Array[String]) {


    val sparkConf = new SparkConf().setAppName("KiiNRE Bridge")
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    ssc.checkpoint("checkpoint")
    ssc.sparkContext.setLogLevel("WARN")
    val homeDir = sys.env("HOME")
    val trustSTorePassword = ssc.sparkContext.getConf.get("spark.ssl.truststore.password")
    val keystorePassword = ssc.sparkContext.getConf.get("spark.ssl.keystore.password")
    val keyPassword = ssc.sparkContext.getConf.get("spark.ssl.key.password")
    
    val nbDir = homeDir + "/spark-notebook/notebooks/streaming" // Path of the certificates
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "development-jp-kafka-0001.internal.kii.com:9093",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "partition.assignment.strategy" -> "org.apache.kafka.clients.consumer.RangeAssignor",
      "group.id" -> "3d73cc26-Xjfc9m0pTYOWYh7ZVYpDXg",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> "false",
      "security.protocol" -> "SSL",
      "ssl.keystore.type" -> "JKS",
      "ssl.truststore.type" -> "JKS",
      "ssl.truststore.location" -> s"${nbDir}/truststore.jks",
      "ssl.truststore.password" -> trustSTorePassword,
      "ssl.keystore.location" -> s"${nbDir}/keystore.jks",
      "ssl.keystore.password" -> keystorePassword,
      "ssl.key.password" -> keyPassword
    )
    val topics = Array("3d73cc26-Xjfc9m0pTYOWYh7ZVYpDXg")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.filter(record => record.key !=null)
     .map(record => (record.key, record.value.toString)).print
    

    ssc.start()
    ssc.awaitTermination()
  }
}