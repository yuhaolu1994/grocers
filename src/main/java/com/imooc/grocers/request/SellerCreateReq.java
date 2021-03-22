package com.imooc.grocers.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SellerCreateReq {

    @NotBlank(message = "empty seller name found")
    private String name;

}
