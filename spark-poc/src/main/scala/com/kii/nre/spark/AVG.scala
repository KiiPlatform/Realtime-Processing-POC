package com.kii.nre.spark
import org.apache.spark.sql.SparkSession
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.apache.spark.sql.types.FloatType
import org.apache.spark.sql.ForeachWriter
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.{avg}

object Average {
    def main(args:Array[String]){
        val spark = SparkSession
                    .builder()
                    .appName("Spark structured streaming Aggregation")
                    .config("spark.eventLog.enabled", "false")
                    .master("local")
                    .getOrCreate()

        import spark.implicits._
        spark.sparkContext.setLogLevel("WARN")

        val kafka = spark.readStream
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("startingOffsets", "latest")
                .option("subscribe", "velocity")
                .load()
        val df = kafka.select($"value".cast("string").cast(FloatType).as("velocity"))
        var writer = new ForeachWriter[Row] {
                
                override def open(partitionId: Long, version: Long): Boolean = {
                        true
                }

                override def process(record: Row) : Unit = {
                    
                    val value = record(0)
                    if(value == null){
                    return
                    }
                    val brokerUrl = "tcp://localhost:1883"
                    val topic = "velocity/avg"

                    //Set up persistence for messages 
                    val persistence = new MemoryPersistence

                    //Initializing Mqtt Client specifying brokerUrl, clientID and MqttClientPersistance
                    val client2 = new MqttClient(brokerUrl, MqttClient.generateClientId, persistence)
                    client2.connect
                    val msgTopic = client2.getTopic(topic)
                    val msg = "%f".format(value.asInstanceOf[Double])
                    
                    val message = new MqttMessage(msg.getBytes("utf-8"))
                    msgTopic.publish(message)
                    client2.disconnect()
                    println("process() :: "+record)
                }

                override def close(errorOrNull: Throwable): Unit = {
                    
                }
        }

        val query = df.filter(df("velocity")>0 || df("velocity")<0)
                .select(avg("velocity"))
                .writeStream.foreach(writer).outputMode("complete")
        .start()
        query.awaitTermination() 

    }
}