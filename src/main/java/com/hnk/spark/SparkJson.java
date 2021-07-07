package com.hnk.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import static org.apache.spark.sql.functions.col;


public class SparkJson {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("spark json")
                .master("local[*]")
//                .config("spark.some.config.option", "some-value")
                .getOrCreate();

        Dataset<Row> json = spark.read().json("/Users/hounaikuo/Documents/project/map-reduce/data/hang.json");
//        json.show();
//        json.printSchema();
//        json.select("account").show();
//        json.select(col("account"), col("id").plus(1)).show();
        json.createOrReplaceTempView("account");
        Dataset<Row> sql = spark.sql("select * from account");
        sql.show();
    }
}
