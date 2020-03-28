package com.imooc.grocers.recommend;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.classification.GBTClassificationModel;
import org.apache.spark.ml.classification.GBTClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;

public class GBDTTrain {
    public static void main(String[] args) throws IOException {
        //初始化spark运行环境
        SparkSession spark = SparkSession.builder().master("local").appName("GrocersApp").getOrCreate();

        //JavaRDD存储以换行符为分隔的string list
        JavaRDD<String> csvFile = spark.read()
                .textFile("file:///Users/yuhaolu/Documents/ElasticSearchSpark/devtool/data/feature.csv")
                .toJavaRDD();

        //做特征转化
        JavaRDD<Row> rowJavaRDD = csvFile.map(new Function<String, Row>() {
            @Override
            public Row call(String s) throws Exception {
                s = s.replace("\"", "");
                String[] strArr = s.split(",");
                //第一列为是否点击，第二列为十一维向量
                //机器学习模型只能接受浮点型
                return RowFactory.create(new Double(strArr[11]), Vectors.dense(Double.valueOf(strArr[0]),Double.valueOf(strArr[1]),
                        Double.valueOf(strArr[2]),Double.valueOf(strArr[3]),Double.valueOf(strArr[4]),Double.valueOf(strArr[5]),
                        Double.valueOf(strArr[6]),Double.valueOf(strArr[7]),Double.valueOf(strArr[8]),Double.valueOf(strArr[9]),Double.valueOf(10)));
            }
        });

        //label:是否点击
        StructType schema = new StructType(new StructField[]{
                new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("features", new VectorUDT(), false, Metadata.empty())
        });

        //将JavaRDD Row和schema关联起来，生成DataSet
        Dataset<Row> data = spark.createDataFrame(rowJavaRDD, schema);

        //80%数据用于训练, 20%数据用于测试
        Dataset<Row>[] dataArr = data.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainData = dataArr[0];
        Dataset<Row> testData = dataArr[1];

        GBTClassifier classifier = new GBTClassifier().setLabelCol("label").setFeaturesCol("features").setMaxIter(10);
        GBTClassificationModel gbtClassificationModel = classifier.train(trainData);

        gbtClassificationModel.save("file:///Users/yuhaolu/Documents/ElasticSearchSpark/devtool/data/gbdtmodel");

        //测试评估
        Dataset<Row> predications = gbtClassificationModel.transform(testData);

        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator();
        double accuracy = evaluator.setMetricName("accuracy").evaluate(predications);
        System.out.println("accuracy="+accuracy);
    }
}
