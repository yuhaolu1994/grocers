package com.imooc.grocers.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RegisterReq {

    @NotBlank(message = "empty telephone found")
    private String telephone;

    @NotBlank(message = "empty password found")
    private String password;

    @NotBlank(message = "empty nickName found")
    private String nickName;

    @NotNull(message = "empty gender found")
    private Integer gender;

}
