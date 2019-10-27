package com.imooc.grocers.service;

import com.imooc.grocers.common.BusinessException;
import com.imooc.grocers.model.ShopModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ShopService {

    ShopModel create(ShopModel shopModel) throws BusinessException;

    ShopModel get(Integer id);

    List<ShopModel> selectAll();

    List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags);

    Integer countAllShop();

    List<ShopModel> recommend(BigDecimal latitude, BigDecimal longitude);

    List<ShopModel> search(BigDecimal latitude, BigDecimal longitude, String keyword, Integer orderby, Integer categoryId, String tags);

}
