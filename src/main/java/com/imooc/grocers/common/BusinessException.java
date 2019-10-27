package com.imooc.grocers.common;

import lombok.Getter;

@Getter
public class BusinessException extends Exception {

    private CommonError commonError;

    public BusinessException(EmBusinessError emBusinessError) {
        super();
        this.commonError = new CommonError(emBusinessError);
    }

    public BusinessException(EmBusinessError emBusinessError, String errorMsg) {
        super();
        this.commonError = new CommonError(emBusinessError);
        this.commonError.setErrorMsg(errorMsg);
    }

}
