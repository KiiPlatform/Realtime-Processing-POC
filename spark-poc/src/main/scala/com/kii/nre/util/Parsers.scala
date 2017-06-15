package com.kii.nre.util

import scala.util.parsing.json._
import com.kii.objects._
import org.apache.spark.streaming.StreamingContext


class KiiNREParser(val key : String, val value : String) {
  private var json:Option[Any] = None
  private var map:Map[String,Any] = Map()
  val objectURI : String = key
  parse()
  
  private def parse() {
    if(value.equalsIgnoreCase("null")) {
      json = None
    }else {
      json = JSON.parseFull(value)
      map = json.get.asInstanceOf[Map[String, Any]]
    }
  }
  def jsonValue : Option[Any] = {
    return json
  }
  def jsonMap = map
}

object URIParser {
  def parse(uri: String) : (KiiBucket,String) ={
    val arr = uri.slice("kiicloud://".length,uri.length).split("/")
    val scope = arr(0)
    val scopeId = arr(1)
    val bucketId = arr(3).slice("rw:".length,arr(3).length)
    val objectId = arr(5)
    (new KiiBucket(scope,scopeId,bucketId),objectId)
  }
}

object ConfigParser {
  def parse(ssc : StreamingContext): (NREConfig,KiiConfig) = {
    //NRE config
    val truststorePassword = ssc.sparkContext.getConf.get("spark.ssl.truststore.password")
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

    (
      new NREConfig(truststorePassword,keystorePassword,keyPassword,kafkaNREbootstrapServers,
                    kafkaNRETopic,kafkaNREGroupId),
      new KiiConfig(host,appID,appKey,accessToken)
    )
  }
}