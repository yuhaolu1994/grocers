package com.imooc.grocers.controller;

import com.imooc.grocers.common.CommonResult;
import com.imooc.grocers.model.CategoryModel;
import com.imooc.grocers.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller("/category")
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ResponseBody
    @RequestMapping("/list")
    public CommonResult list() {
        List<CategoryModel> categoryModelList = categoryService.selectAll();
        return CommonResult.create(categoryModelList);
    }

}
