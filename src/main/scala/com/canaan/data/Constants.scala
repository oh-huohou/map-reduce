package com.canaan.data

object Constants {
  val projectPath = "/Users/hounaikuo/Documents/gitlab/map-reduce"
  val protocol = "file:///"
  val rawPath = projectPath + "/data/miner_log.log" // 原始数据
//  val rawPath = projectPath + "/data/access.1w.log" // 原始数据
  val tempOut = projectPath + "/data/etl" // 第一步清洗
  val cleanedIn = tempOut + "/part-*" // 第二次清洗的输入路径
  val streamOut = projectPath + "/data/stream" // stream清洗的数据
}
