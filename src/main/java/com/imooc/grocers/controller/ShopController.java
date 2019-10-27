package com.imooc.grocers.controller;

import com.imooc.grocers.common.BusinessException;
import com.imooc.grocers.common.CommonResult;
import com.imooc.grocers.common.EmBusinessError;
import com.imooc.grocers.model.CategoryModel;
import com.imooc.grocers.model.ShopModel;
import com.imooc.grocers.service.CategoryService;
import com.imooc.grocers.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller("/shop")
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private CategoryService categoryService;

    //推荐服务V1.0
    @RequestMapping("/recommend")
    @ResponseBody
    public CommonResult recommend(@RequestParam(value = "longitude") BigDecimal longitude,
                                  @RequestParam(value = "latitude") BigDecimal latitude) throws BusinessException {
        if (Objects.isNull(latitude) || Objects.isNull(longitude)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        List<ShopModel> shopModelList = shopService.recommend(latitude, longitude);
        return CommonResult.create(shopModelList);
    }

    //搜索服务V1.0
    @RequestMapping("/search")
    @ResponseBody
    public CommonResult search(@RequestParam(value = "longitude") BigDecimal longitude,
                               @RequestParam(value = "latitude") BigDecimal latitude,
                               @RequestParam(value = "keyword") String keyword,
                               @RequestParam(value = "orderby", required = false) Integer orderby,
                               @RequestParam(value = "categoryId", required = false) Integer categoryId,
                               @RequestParam(value = "tags", required = false) String tags) throws BusinessException {
        if (Objects.isNull(latitude) || Objects.isNull(longitude) || StringUtils.isEmpty(keyword)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        List<ShopModel> shopModelList = shopService.search(latitude, longitude, keyword, orderby, categoryId, tags);
        List<CategoryModel> categoryModelList = categoryService.selectAll();
        List<Map<String, Object>> tagsAggregation = shopService.searchGroupByTags(keyword, categoryId, tags);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("shop", shopModelList);
        resMap.put("category", categoryModelList);
        resMap.put("tags", tagsAggregation);
        return CommonResult.create(resMap);
    }

}
