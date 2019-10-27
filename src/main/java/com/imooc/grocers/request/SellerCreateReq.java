package com.imooc.grocers.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SellerCreateReq {

    @NotBlank(message = "商户名不能为空")
    private String name;

}
