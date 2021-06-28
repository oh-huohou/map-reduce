package com.hnk.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.regex.Pattern;

public class SparkApplication {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("sparkBoot").setMaster("local");

        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        JavaRDD<String> lines = sparkContext.textFile("/Users/hounaikuo/Documents/project/map-reduce/data/word").cache();

        lines.map((Function<String, Object>) s -> s);

        JavaRDD<String> words = lines.flatMap((FlatMapFunction<String, String>) s -> Arrays.asList(SPACE.split(s)).iterator());

        JavaPairRDD<String, Integer> wordsOnes = words.mapToPair((PairFunction<String, String, Integer>) s -> new Tuple2<String, Integer>(s, 1));

        JavaPairRDD<String, Integer> wordsCounts = wordsOnes.reduceByKey((Function2<Integer, Integer, Integer>) (value, toValue) -> value + toValue);

        wordsCounts.saveAsTextFile("/Users/hounaikuo/Documents/project/map-reduce/data/fordata");
    }


}