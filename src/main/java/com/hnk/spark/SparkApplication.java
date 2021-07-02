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
        run(sparkContext);
    }

    public static void run(JavaSparkContext sparkContext) {
        JavaRDD<String> rdd = sparkContext.parallelize(Arrays.asList("t est", "t ava", "p ython"));

        //把RDD的第一个字符当做Key
        PairFunction<String, String, String> pairFunction = new PairFunction<String, String, String>() {
            @Override
            public Tuple2<String, String> call(String s) throws Exception {
                return new Tuple2<>(s.split(" ")[0], s);
            }
        };


        //此处创建好pairRDD
        JavaPairRDD<String, String> pairRdd = rdd.mapToPair(pairFunction);

        //下层都是对pairRDD的操作演示

        /*合并含有相同键的值*/
        JavaPairRDD<String, String> rdd1 = pairRdd.reduceByKey((v1, v2) -> v1 + v2).groupByKey().mapValues(v1 -> v1 + "sirZ");

        /*相同key的元素进行分组*/
//        pairRdd.groupByKey();
//
//        /*对pair中的每个值进行应用*/
//        pairRdd.mapValues( v1 -> v1 + "sirZ");

        /*返回只包含键的RDD*/
        pairRdd.keys();

        /*返回只包含值的RDD*/
        pairRdd.values();

        /*返回根据键排序的RDD*/
        pairRdd.sortByKey();

        rdd1.foreach(elem -> {
            System.out.println(elem._1 + "-----" + elem._2);
        });
    }

    public static void wordCount(JavaSparkContext sparkContext){

        JavaRDD<String> textFile = sparkContext.textFile("/Users/hounaikuo/Documents/project/map-reduce/data/word");
        JavaPairRDD<String, Integer> counts = textFile
                .flatMap(s -> Arrays.asList(s.split(" ")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);
        counts.saveAsTextFile("/Users/hounaikuo/Documents/project/map-reduce/data/fordata");
    }

}
