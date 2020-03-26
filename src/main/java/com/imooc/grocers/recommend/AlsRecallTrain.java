package com.imooc.grocers.recommend;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.Serializable;

// Spark可以在不同机器上运行，实现远程调用
public class AlsRecallTrain implements Serializable {

    public static void main(String[] args) throws IOException {
        //初始化spark运行环境
        SparkSession spark = SparkSession.builder().master("local").appName("GrocersApp").getOrCreate();

        //JavaRDD存储以换行符为分隔的string list
        JavaRDD<String> csvFile = spark.read()
                .textFile("file:///Users/yuhaolu/Documents/ElasticSearchSpark/devtool/data/behavior.csv")
                .toJavaRDD(); //Spark原生语言是Scala

        //这里map类似java stream
        JavaRDD<Rating> ratingJavaRDD = csvFile.map(new Function<String, Rating>() {
            @Override
            public Rating call(String v1) throws Exception {
                return Rating.parseRating(v1);
            }
        });

        //以Rating为字段的数据表
        Dataset<Row> rating = spark.createDataFrame(ratingJavaRDD, Rating.class);

        //80%数据用于train, 20%数据用于测试
        Dataset<Row>[] splits = rating.randomSplit(new double[]{0.8, 0.2});

        Dataset<Row> trainingData = splits[0];
        Dataset<Row> testingData = splits[1];

        //过拟合：增大数据规模，减少RANK，增大正则化的系数
        //欠拟合：增加rank，减少正则化系数
        ALS als = new ALS().setMaxIter(10).setRank(5).setRegParam(0.01)
                .setUserCol("userId").setItemCol("shopId").setRatingCol("rating");

        //模型训练
        ALSModel alsModel = als.fit(trainingData);

        //模型评测
        Dataset<Row> predictions = alsModel.transform(testingData);

        //rmse均方根误差，预测值与真实值的偏差的平方除以观测次数，开个根号
        RegressionEvaluator evaluator = new RegressionEvaluator().setMetricName("rmse")
                .setLabelCol("rating").setPredictionCol("prediction");
        double rmse = evaluator.evaluate(predictions);
        System.out.println("rmse = " + rmse);

        alsModel.save("file:///Users/yuhaolu/Documents/ElasticSearchSpark/devtool/data/alsmodel");
    }

    public static class Rating implements Serializable {
        private int userId;
        private int shopId;
        private int rating;

        public static Rating parseRating(String str) {
            str = str.replace("\"", "");
            String[] strArr = str.split(",");
            int userId = Integer.parseInt(strArr[0]);
            int shopId = Integer.parseInt(strArr[1]);
            int rating = Integer.parseInt(strArr[2]);
            return new Rating(userId, shopId, rating);
        }

        public Rating(int userId, int shopId, int rating) {
            this.userId = userId;
            this.shopId = shopId;
            this.rating = rating;
        }

        public int getUserId() {
            return userId;
        }

        public int getShopId() {
            return shopId;
        }

        public int getRating() {
            return rating;
        }
    }
}
