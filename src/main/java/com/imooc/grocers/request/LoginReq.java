package com.imooc.grocers.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginReq {

    @NotBlank(message = "empty phone num found")
    private String telephone;

    @NotBlank(message = "empty password found")
    private String password;

}
