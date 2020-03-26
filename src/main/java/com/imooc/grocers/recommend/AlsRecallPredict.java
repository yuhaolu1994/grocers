package com.imooc.grocers.recommend;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class AlsRecallPredict implements Serializable {

    public static void main(String[] args) throws IOException, SQLException {
        //初始化spark运行环境
        SparkSession spark = SparkSession.builder().master("local").appName("GrocersApp").getOrCreate();

        //加载模型进内存，预测活跃用户并生成离线召回集
        ALSModel alsModel = ALSModel.load("file:///Users/yuhaolu/Documents/ElasticSearchSpark/devtool/data/alsmodel");

        JavaRDD<String> csvFile = spark.read()
                .textFile("file:///Users/yuhaolu/Documents/ElasticSearchSpark/devtool/data/behavior.csv")
                .toJavaRDD();

        JavaRDD<Rating> ratingJavaRDD = csvFile.map(new Function<String, Rating>() {
            @Override
            public Rating call(String v1) throws Exception {
                return Rating.parseRating(v1);
            }
        });

        Dataset<Row> rating = spark.createDataFrame(ratingJavaRDD, Rating.class);

        //给5个活跃用户做离线的召回结果预测
        Dataset<Row> users = rating.select(alsModel.getUserCol()).distinct().limit(5); //5个不同用户的行数据
        //给5个不同用户每个推荐20个产品
        //如果用recommendForAllUsers，表示对training集合中的所有用户做指定产品数量的推荐
        Dataset<Row> userRecs = alsModel.recommendForUserSubset(users, 20);

        //用分片的方式做数据库的入库
        //userRecs中有5条记录，做数据处理分片在不同机器上
        //spark会并行跑在不同的节点上
        userRecs.foreachPartition(new ForeachPartitionFunction<Row>() {
            //一个partition会完成一次数据库的批量插入
            @Override
            public void call(Iterator<Row> iterator) throws Exception {

                //新建数据库连接
                Connection connection = DriverManager
                        .getConnection("jdbc:mysql://127.0.0.1:3306/grocers?" +
                                "user=root&password=luyuhao@1994&useUnicode=true&characterEncoding=UTF-8");
                PreparedStatement preparedStatement = connection.prepareStatement("insert into recommend(id, recommend) values(?,?)");

                List<Map<String, Object>> data = new ArrayList<>();

                //对list进行遍历
                iterator.forEachRemaining(action -> {
                    int userId = action.getInt(0);
                    List<GenericRowWithSchema> recommendationList = action.getList(1);
                    List<Integer> shopIdList = new ArrayList<>();
                    recommendationList.forEach(row -> {
                        Integer shopId = row.getInt(0);
                        shopIdList.add(shopId);
                    });
                    String recommendData = StringUtils.join(shopIdList, ",");
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", userId);
                    map.put("recommend", recommendData);
                    data.add(map);
                });

                data.forEach(stringObjectMap -> {
                    try {
                        preparedStatement.setInt(1, (Integer)stringObjectMap.get("userId"));
                        preparedStatement.setString(2, (String)stringObjectMap.get("recommend"));
                        preparedStatement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                preparedStatement.executeBatch(); //批量插入
                connection.close(); //关闭连接
            }
        });
    }

    //运行在不同进程中
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
