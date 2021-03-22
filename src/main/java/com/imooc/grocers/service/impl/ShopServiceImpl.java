package com.imooc.grocers.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.imooc.grocers.common.BusinessException;
import com.imooc.grocers.common.EmBusinessError;
import com.imooc.grocers.dal.ShopModelMapper;
import com.imooc.grocers.model.CategoryModel;
import com.imooc.grocers.model.SellerModel;
import com.imooc.grocers.model.ShopModel;
import com.imooc.grocers.service.CategoryService;
import com.imooc.grocers.service.SellerService;
import com.imooc.grocers.service.ShopService;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopModelMapper shopModelMapper;

    @Autowired
    private RestHighLevelClient highLevelClient;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public ShopModel create(ShopModel shopModel) throws BusinessException {
        SellerModel sellerModel = sellerService.get(shopModel.getSellerId());
        if (Objects.isNull(sellerModel)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "seller doesn't exist");
        }
        if (sellerModel.getDisabledFlag().intValue() == 1) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "seller is recalled");
        }

        CategoryModel categoryModel = categoryService.get(shopModel.getCategoryId());
        if (Objects.isNull(categoryModel)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "category doesn't exist");
        }

        shopModel.setCreatedAt(new Date());
        shopModel.setUpdatedAt(new Date());
        shopModelMapper.insertSelective(shopModel);
        return get(shopModel.getId());
    }

    @Override
    public ShopModel get(Integer id) {
        ShopModel shopModel = shopModelMapper.selectByPrimaryKey(id);
        if (Objects.isNull(shopModel)) {
            return null;
        }
        shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
        shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
        return shopModel;
    }

    @Override
    public Map<String, Object> searchES(BigDecimal latitude, BigDecimal longitude, String keyword, Integer orderBy, Integer categoryId, String tags) throws IOException {
        Map<String, Object> result = new HashMap<>();

        Request request = new Request("GET", "/shop/_search");

        JSONObject jsonRequestObj = new JSONObject();

        jsonRequestObj.put("_source", "*");

        // building distance field
        jsonRequestObj.put("script_fields", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").put("distance", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").put("script", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("source", "haversin(lat, lon, doc['location'].lat, doc['location'].lon)");
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("lang", "expression");
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("params", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .getJSONObject("params").put("lat", latitude);
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .getJSONObject("params").put("lon", longitude);

        // assemble query
        jsonRequestObj.put("query", new JSONObject());

        // build function score
        jsonRequestObj.getJSONObject("query").put("function_score", new JSONObject());

        // build query in function score
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("query", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").put("bool", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").put("must", new JSONArray());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").add(new JSONObject());

        // build match query
        int queryIndex = 0;

        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("match", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").put("name", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name").put("query", keyword);
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name").put("boost", 0.1);

        queryIndex++;

        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").add(new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("seller_disabled_flag", 0);

        if (tags != null) {
            queryIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("tags", tags);
        }

        if (categoryId != null) {
            queryIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("category_id", categoryId);
        }


        // build functions
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("functions", new JSONArray());

        int functionIndex = 0;
        if (orderBy == null) {
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("gauss", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss").put("location", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("origin", latitude.toString() + "," + longitude.toString());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("scale", "100km");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("offset", "0km");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("decay", "0.5");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight", 9);

            functionIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field", "remark_score");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight", 0.2);

            functionIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field", "seller_remark_score");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight", 0.1);

            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode", "sum");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode", "sum");
        } else {
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field", "price_per_man");

            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode", "sum");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode", "replace");
        }

        jsonRequestObj.put("sort", new JSONArray());
        jsonRequestObj.getJSONArray("sort").add(new JSONObject());
        jsonRequestObj.getJSONArray("sort").getJSONObject(0).put("_score", new JSONObject());
        if (orderBy == null) {
            jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order", "desc");
        } else {
            jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order", "asc");
        }

        jsonRequestObj.put("aggs", new JSONObject());
        jsonRequestObj.getJSONObject("aggs").put("group_by_tags", new JSONObject());
        jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").put("terms", new JSONObject());
        jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").getJSONObject("terms").put("field", "tags");

        String reqJson = jsonRequestObj.toJSONString();

        request.setJsonEntity(reqJson);
        Response response = highLevelClient.getLowLevelClient().performRequest(request);
        String responseStr = EntityUtils.toString(response.getEntity());

        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        JSONArray jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
        List<ShopModel> shopModelList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            Integer id = new Integer(jsonObj.get("_id").toString());
            BigDecimal distance = new BigDecimal(jsonObj.getJSONObject("fields").getJSONArray("distance").get(0).toString());
            ShopModel shopModel = get(id);
            shopModel.setDistance(distance.multiply(new BigDecimal(1000).setScale(0, BigDecimal.ROUND_CEILING)).intValue());
            shopModelList.add(shopModel);
        }
        result.put("shop", shopModelList);

        List<Map> tagsList = new ArrayList<>();
        JSONArray tagsJsonArray = jsonObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
        for (int i = 0; i < tagsJsonArray.size(); i++) {
            JSONObject jsonObj = tagsJsonArray.getJSONObject(i);
            Map<String, Object> tagMap = new HashMap<>();
            tagMap.put("tags", jsonObj.getString("key"));
            tagMap.put("num", jsonObj.getInteger("doc_count"));
            tagsList.add(tagMap);
        }
        result.put("tags", tagsList);

        return result;
    }

}
