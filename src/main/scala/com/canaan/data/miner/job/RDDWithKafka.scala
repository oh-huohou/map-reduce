package com.canaan.data.miner.job

import com.canaan.data.MinerLog
import com.canaan.lib.core.ObjectMapperWrapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{KafkaUtils, OffsetRange}

import scala.collection.JavaConversions

object RDDWithKafka {
  private val brokers = "10.8.0.50:9092"
  private val topic = "kafka.replyinfo"
  private val group = "map-reduce"
  //  private val group = "receiver"

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("MinerEtlJob").master("local[*]").getOrCreate()

    val ssc = spark.sparkContext

    //    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDDWithKafka")
    //    val ssc = new SparkContext(sparkConf)

    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.GROUP_ID_CONFIG -> group
    )

    val offsetRanges = Array(
      OffsetRange(topic, 0, 650, 660)
      //      OffsetRange(topic, 1, 0, 100),
      //      OffsetRange(topic, 2, 0, 100),
      //      OffsetRange(topic, 3, 0, 100)
    )
    val rdd = KafkaUtils.createRDD(ssc,
      JavaConversions.mapAsJavaMap(kafkaParams),
      offsetRanges, PreferConsistent)
    rdd.foreachPartition(records => {
      records.foreach(record => {
        println(record.topic() + ":" + record.partition() + ":" + record.value())

        val minerLog = ObjectMapperWrapper.toObject(record.value(), classOf[MinerLog])

        println(record.topic() + ":" + record.partition() + ":" + minerLog)
      })
    })
  }
}
