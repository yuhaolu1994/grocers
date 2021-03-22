package com.imooc.grocers.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CategoryCreateReq {

    @NotBlank(message = "empty name found")
    private String name;

    @NotBlank(message = "empty iconUrl found")
    private String iconUrl;

    @NotNull(message = "null weight found")
    private Integer sort;

}
