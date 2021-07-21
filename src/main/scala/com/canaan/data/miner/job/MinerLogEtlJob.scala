package com.canaan.data.miner.job

import com.canaan.data.{Constants, MinerLog}
import com.canaan.data.util.DirUtil
import com.canaan.lib.core.ObjectMapperWrapper
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


object MinerLogEtlJob {
  def main(args: Array[String]): Unit = {
    // 删除结果目录
    DirUtil.deleteDir(Constants.tempOut)

    val spark: SparkSession = SparkSession.builder().appName("MinerEtlJob").master("local[*]").getOrCreate()

    val accessFile: RDD[String] = spark.sparkContext.textFile(Constants.protocol + Constants.rawPath)

    accessFile.map(line => {

      try {
        ObjectMapperWrapper.toObject(line, classOf[MinerLog])
      } catch {
        case e: Exception => e.printStackTrace()
      }

    }).saveAsTextFile(Constants.protocol + Constants.tempOut)

    spark.stop()
  }
}
