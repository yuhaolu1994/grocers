package com.imooc.grocers.service;

import com.imooc.grocers.common.BusinessException;
import com.imooc.grocers.model.SellerModel;

import java.util.List;

public interface SellerService {

    SellerModel create(SellerModel sellerModel);

    SellerModel get(Integer id);

    List<SellerModel> selectAll();

    SellerModel changeStatus(Integer id, Integer disabledFlag) throws BusinessException;

    Integer countAllSeller();

}
