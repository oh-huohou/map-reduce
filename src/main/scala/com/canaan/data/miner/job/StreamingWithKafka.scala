package com.canaan.data.miner.job

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamingWithKafka {
  private val brokers = "10.8.0.50:9092"
  private val topic = Set("kafka.replyinfo")
  private val group = "map-reduce"
  //  private val group = "receiver"

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("MinerEtlJob").master("local[*]").getOrCreate()

    val ssc = new StreamingContext(spark.sparkContext, Seconds(5));

    //    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDDWithKafka")
//    # 环境变量spark-defaults.conf的配置请参见搭建开发环境。
//    cd $SPARK_HOME
    //    bin/spark-submit --master yarn-cluster --class com.aliyun.odps.spark.examples.streaming.kafka.KafkaStreamingDemo /path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar

    //    val ssc = new SparkContext(sparkConf)

    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.GROUP_ID_CONFIG -> group
    )

    val recordDstream = KafkaUtils.createDirectStream(
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topic, kafkaParams)
      //      JavaConversions.mapAsJavaMap(kafkaParams),
      //      offsetRanges, PreferConsistent)
    )
    val dstream = recordDstream.map(f => (f.key(), f.value()))
    dstream.map(k => {
      println(k._1)
      println(k._2)
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
