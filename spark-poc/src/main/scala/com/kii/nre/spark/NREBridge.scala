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
import scala.util.parsing.json._
import scalaj.http._
import org.apache.spark.rdd.RDD
import com.kii.nre.util._
import com.kii.objects._

object KiiNREBridge {
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("KiiNRE Bridge")
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    ssc.checkpoint("checkpoint")
    ssc.sparkContext.setLogLevel("WARN")
    val homeDir = sys.env("HOME")
    val conf = ConfigParser.parse(ssc)
    //NRE config
    val nreConf = conf._1
    //KII config
    val kiiConf = conf._2
    
    val group = "vehicle-monitoring"
    val confBucket = "config"
    val url = "https://%s/api/apps/%s/groups/%s/buckets/%s/objects/velocity-chart"
              .format(kiiConf.host,kiiConf.appID,group,confBucket)
    println(s"***************************** $url")
    //load
    val response=Http(url)
      .method("GET")
      .header("Authorization", "Bearer %s".format(kiiConf.accessToken))
      .header("Content-Type", "application/json") 
    
    val chartConfig = JSON.parseFull(response.asString.body).get.asInstanceOf[Map[String, Any]]
    val chartType = chartConfig("chart_type").toString

    println(s"***************************** $chartType")
    
    val nbDir = homeDir + "/spark-notebook/notebooks/streaming" // Path of the certificates
    val kafkaParams = KafkaHelper.generateParams(nreConf,nbDir)
    val topics = Array(nreConf.kafkaNRETopic)
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    val bucketToFilter = new KiiBucket("groups","vehicle-monitoring","can-logger")
    val messages = stream
      .filter(record => record.key !=null && record.value !=null)
      .filter(record => BucketFilter.filter(bucketToFilter,record.key.toString,record.value.toString))
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
    println(original._1)
    return true
  }
}