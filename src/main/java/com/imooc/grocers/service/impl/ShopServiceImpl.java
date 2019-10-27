package com.imooc.grocers.service.impl;

import com.imooc.grocers.common.BusinessException;
import com.imooc.grocers.common.EmBusinessError;
import com.imooc.grocers.dal.ShopModelMapper;
import com.imooc.grocers.model.CategoryModel;
import com.imooc.grocers.model.SellerModel;
import com.imooc.grocers.model.ShopModel;
import com.imooc.grocers.service.CategoryService;
import com.imooc.grocers.service.SellerService;
import com.imooc.grocers.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopModelMapper shopModelMapper;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public ShopModel create(ShopModel shopModel) throws BusinessException {
        //校验商家
        SellerModel sellerModel = sellerService.get(shopModel.getSellerId());
        if (Objects.isNull(sellerModel)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商户不存在");
        }
        if (sellerModel.getDisabledFlag().intValue() == 1) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商户已禁用");
        }

        //校验类目
        CategoryModel categoryModel = categoryService.get(shopModel.getCategoryId());
        if (Objects.isNull(categoryModel)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "类目不存在");
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
        shopModel.setSellerModel(sellerService.get(shopModel.getSellerId())); //关联对应的seller和category信息
        shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
        return shopModel;
    }

    @Override
    public List<ShopModel> selectAll() {
        List<ShopModel> shopModelList = shopModelMapper.selectAll();
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId())); //关联对应的seller和category信息
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags) {
        return shopModelMapper.searchGroupByTags(keyword, categoryId, tags);
    }

    @Override
    public Integer countAllShop() {
        return shopModelMapper.countAllShop();
    }

    @Override
    public List<ShopModel> recommend(BigDecimal latitude, BigDecimal longitude) {
        return shopModelMapper.recommend(latitude, longitude);
    }

    @Override
    public List<ShopModel> search(BigDecimal latitude, BigDecimal longitude, String keyword, Integer orderby,
                                  Integer categoryId, String tags) {
        List<ShopModel> shopModelList = shopModelMapper.search(latitude, longitude, keyword, orderby, categoryId, tags);
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId())); //关联对应的seller和category信息
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

}
