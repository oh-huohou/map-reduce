package com.canaan.data.miner.job

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaStreamingDemo {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("KafkaStreamingDemo")
      .getOrCreate()

    val ssc: StreamingContext = new StreamingContext(spark.sparkContext, Seconds(5))

    // 请使用OSS作为Checkpoint存储
    //    ssc.checkpoint("oss://bucket/checkpointDir/")
    ssc.checkpoint("file://bucket/checkpointDir/")

    // kafka配置参数
    val kafkaParams: Map[String, Object] = Map[String, Object](
      "bootstrap.servers" -> "10.8.0.50:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "testGroupId",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Set("kafka.replyinfo")

    val recordDstream: InputDStream[ConsumerRecord[String, String]] =
      KafkaUtils.createDirectStream[String, String](
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
      )


    val dstream = recordDstream.map(f => (f.key(), f.value()))
    dstream.map(k => {
      println(k._1)
      println(k._2)
    })
//    val data: DStream[String] = dstream.map(_._2)
//    val wordsDStream: DStream[String] = data.flatMap(_.split(" "))
//    val wordAndOneDstream: DStream[(String, Int)] = wordsDStream.map((_, 1))
//    val result: DStream[(String, Int)] = wordAndOneDstream.reduceByKey(_ + _)
//    result.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
