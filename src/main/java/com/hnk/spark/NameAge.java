package com.hnk.spark;

import com.clearspring.analytics.util.Lists;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.List;

public class NameAge {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().appName("nameAge").master("local[*]").getOrCreate();

        JavaRDD<String> peopleRDD = spark.sparkContext().textFile("data/nameAge", 1).toJavaRDD();

        //使用string定义schema
        String schemaString = "name age";

        //基于用字符串定义的schema生成StructType
        List<StructField> fields = Lists.newArrayList();
        for (String fieldName : schemaString.split(" ")) {
            StructField field = DataTypes.createStructField(fieldName, DataTypes.StringType, true);
            fields.add(field);
        }
        StructType schema = DataTypes.createStructType(fields);

        JavaRDD<Row> rowRdd = peopleRDD.map(record -> {
            String[] attributes = record.split(",");
            Row row = RowFactory.create(attributes[0], attributes[1].trim());
            return row;
        });

        Dataset<Row> dataFrame = spark.createDataFrame(rowRdd, schema);
        dataFrame.createOrReplaceTempView("people");

        Dataset<Row> results = spark.sql("select name from people");

        Dataset<String> namesDS = results.map((MapFunction<Row, String>) row -> "Name: " + row.getString(0), Encoders.STRING());
        namesDS.show();


    }
}
