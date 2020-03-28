package com.imooc.grocers.recommend;

import org.apache.spark.ml.classification.GBTClassificationModel;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendSortService {

    private SparkSession spark;

    private LogisticRegressionModel lrModel;

    private GBTClassificationModel gbtClassificationModel;

    //bean初始化之后
    @PostConstruct
    public void init() {
        //加载LR模型
        spark = SparkSession.builder().master("local").appName("GrocersApp").getOrCreate();

        lrModel = LogisticRegressionModel.load("file:///Users/yuhaolu/Documents/ElasticSearchSpark/devtool/data/lrmodel");

        gbtClassificationModel = GBTClassificationModel.load("file:///Users/yuhaolu/Documents/ElasticSearchSpark/devtool/data/gbdtmodel");
    }

    public List<Integer> sort(List<Integer> shopIdList, Integer userId) {
        //根据lrmodel所需要的11维的x,生成特征,然后调用其预测方法
        List<ShopSortModel> list = new ArrayList<>();
        for (Integer shopId : shopIdList) {
            //造的假数据，可以从数据库或缓存中拿到对应的性别，年龄，评分，价格等做特征转化生成feature向量
            Vector v = Vectors.dense(1, 0, 0, 0, 0, 1, 0.6, 0, 0, 1, 0);
//            Vector result = lrModel.predictProbability(v);
            Vector result = gbtClassificationModel.predictProbability(v);
            double[] arr = result.toArray();
            double score = arr[1]; //取正值
            ShopSortModel shopSortModel = new ShopSortModel();
            shopSortModel.setShopId(shopId);
            shopSortModel.setScore(score);
            list.add(shopSortModel);
        }
        //由大到小排列
        list.sort(new Comparator<ShopSortModel>() {
            @Override
            public int compare(ShopSortModel o1, ShopSortModel o2) {
                if (o1.getScore() < o2.getScore()) {
                    return 1;
                } else if (o1.getScore() > o2.getScore()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return list.stream().map(shopSortModel -> shopSortModel.getShopId()).collect(Collectors.toList());
    }
}
