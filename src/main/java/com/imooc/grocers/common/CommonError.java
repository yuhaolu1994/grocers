package com.imooc.grocers.common;

import lombok.Data;

@Data
public class CommonError {

    private Integer errorCode;

    private String errorMsg;

    public CommonError(EmBusinessError emBusinessError) {
        this.errorCode = emBusinessError.getErrorCode();
        this.errorMsg = emBusinessError.getErrorMsg();
    }
}
