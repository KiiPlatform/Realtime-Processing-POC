package com.kii.nre.spark

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.{StringDeserializer,StringSerializer}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.kafka.clients.consumer.RangeAssignor
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext, Duration, Seconds}
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import java.util.Properties
import com.github.benfradet.spark.kafka010.writer._
import org.apache.kafka.common.serialization.StringSerializer
import scala.util.parsing.json._
import scalaj.http._
import org.apache.spark.rdd.RDD

object KiiNREBridge {
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("KiiNRE Bridge")
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    ssc.checkpoint("checkpoint")
    ssc.sparkContext.setLogLevel("WARN")
    val homeDir = sys.env("HOME")
    //NRE config
    val trustSTorePassword = ssc.sparkContext.getConf.get("spark.ssl.truststore.password")
    val keystorePassword = ssc.sparkContext.getConf.get("spark.ssl.keystore.password")
    val keyPassword = ssc.sparkContext.getConf.get("spark.ssl.key.password")
    val kafkaNREbootstrapServers = ssc.sparkContext.getConf.get("spark.nre.kafka.bootstrap.servers")
    val kafkaNRETopic = ssc.sparkContext.getConf.get("spark.nre.kafka.topic.id")
    val kafkaNREGroupId = ssc.sparkContext.getConf.get("spark.nre.kafka.group.id")

    //KII config
    val host = ssc.sparkContext.getConf.get("spark.kii.app.host")
    val appID = ssc.sparkContext.getConf.get("spark.kii.app.id")
    val accessToken = ssc.sparkContext.getConf.get("spark.kii.user.accesstoken")
    val appKey = ssc.sparkContext.getConf.get("spark.kii.app.key")
    val group = "vehicle-monitoring"
    val confBucket = "config"

    //load
    val response=Http(s"https://$host/api/apps/$appID/groups/$group/buckets/$confBucket/objects/velocity-chart")
      .method("GET")
      .header("Authorization", s"Bearer $accessToken")
      .header("Content-Type", "application/json") 
    
    val chartConfig = JSON.parseFull(response.asString.body).get.asInstanceOf[Map[String, Any]]
    val chartType = chartConfig("chart_type").toString

    println(s"***************************** $chartType")
    
    val nbDir = homeDir + "/spark-notebook/notebooks/streaming" // Path of the certificates
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> kafkaNREbootstrapServers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "partition.assignment.strategy" -> "org.apache.kafka.clients.consumer.RangeAssignor",
      "group.id" -> kafkaNREGroupId,
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
    val topics = Array(kafkaNRETopic)
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    
    val messages = stream.filter(record => record.key !=null && record.value !=null)
     .map(record => (record.key.toString, record.value.toString))

    //Kafka Producer 
    val topic = "test"
    val producerConfig = {
      val p = new java.util.Properties()
      p.setProperty("bootstrap.servers", "127.0.0.1:9092")
      p.setProperty("key.serializer", classOf[StringSerializer].getName)
      p.setProperty("value.serializer", classOf[StringSerializer].getName)
      p
    }
    messages.foreachRDD( rdd => {
      val filtered = rdd.filter(this.filterNRE)
      filtered.writeToKafka(
        producerConfig,
        s => new ProducerRecord[String, String](topic, s._1,s._2)
      )
    })
    ssc.start()
    ssc.awaitTermination() 
  }

  def filterNRE(original: ((String,String))) : Boolean ={
    return true
  }
}