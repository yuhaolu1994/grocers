package com.imooc.grocers.service;

import com.imooc.grocers.common.BusinessException;
import com.imooc.grocers.model.ShopModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public interface ShopService {

    ShopModel create(ShopModel shopModel) throws BusinessException;

    ShopModel get(Integer id);

    Map<String, Object> searchES(BigDecimal latitude, BigDecimal longitude, String keyword, Integer orderBy, Integer categoryId, String tags) throws IOException;

}
