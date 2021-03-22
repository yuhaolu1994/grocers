package com.imooc.grocers.controller.admin;

import com.imooc.grocers.common.*;
import com.imooc.grocers.model.SellerModel;
import com.imooc.grocers.request.SellerCreateReq;
import com.imooc.grocers.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller("/admin/seller")
@RequestMapping("/admin/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @AdminPermission
    @ResponseBody
    public CommonResult create(@Valid SellerCreateReq sellerCreateReq, BindingResult bindingResult) throws BusinessException {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processErrorString(bindingResult));
        }

        SellerModel sellerModel = new SellerModel();
        sellerModel.setName(sellerCreateReq.getName());
        sellerService.create(sellerModel);

        return CommonResult.create(sellerModel);
    }

    @RequestMapping(value = "/down", method = RequestMethod.POST)
    @AdminPermission
    @ResponseBody
    public CommonResult down(@RequestParam(value = "id") Integer id) throws BusinessException {
        SellerModel sellerModel = sellerService.changeStatus(id, 1);
        return CommonResult.create(sellerModel);
    }

    @RequestMapping(value = "/up", method = RequestMethod.POST)
    @AdminPermission
    @ResponseBody
    public CommonResult up(@RequestParam(value = "id") Integer id) throws BusinessException {
        SellerModel sellerModel = sellerService.changeStatus(id, 0);
        return CommonResult.create(sellerModel);
    }

}
