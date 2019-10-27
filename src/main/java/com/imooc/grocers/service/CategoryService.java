package com.imooc.grocers.service;

import com.imooc.grocers.common.BusinessException;
import com.imooc.grocers.model.CategoryModel;

import java.util.List;

public interface CategoryService {

    CategoryModel create(CategoryModel categoryModel) throws BusinessException;

    CategoryModel get(Integer id);

    List<CategoryModel> selectAll();

    Integer countAllCategory();

}
