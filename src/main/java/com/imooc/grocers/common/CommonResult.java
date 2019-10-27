package com.imooc.grocers.common;

import lombok.Data;

@Data
public class CommonResult {

    private String status;

    private Object data;

    public static CommonResult create(Object result) {
        return CommonResult.create(result, "success");
    }

    public static CommonResult create(Object result, String status) {
        CommonResult commonResult = new CommonResult();
        commonResult.setStatus(status);
        commonResult.setData(result);
        return commonResult;
    }

}
