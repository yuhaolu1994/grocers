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

import java.io.IOException;
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

    @RequestMapping("/search")
    @ResponseBody
    public CommonResult search(@RequestParam(value = "longitude") BigDecimal longitude,
                               @RequestParam(value = "latitude") BigDecimal latitude,
                               @RequestParam(value = "keyword") String keyword,
                               @RequestParam(value = "orderBy", required = false) Integer orderBy,
                               @RequestParam(value = "categoryId", required = false) Integer categoryId,
                               @RequestParam(value = "tags", required = false) String tags) throws BusinessException, IOException {
        if (Objects.isNull(latitude) || Objects.isNull(longitude) || StringUtils.isEmpty(keyword)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        Map<String, Object> result = shopService.searchES(latitude, longitude, keyword, orderBy, categoryId, tags);
        List<ShopModel> shopModelList = (List<ShopModel>) result.get("shop");
        List<Map<String,Object>> tagsAggregation = (List<Map<String, Object>>) result.get("tags");
        List<CategoryModel> categoryModelList = categoryService.selectAll();

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("shop", shopModelList);
        resMap.put("category", categoryModelList);
        resMap.put("tags", tagsAggregation);

        return CommonResult.create(resMap);
    }

}
