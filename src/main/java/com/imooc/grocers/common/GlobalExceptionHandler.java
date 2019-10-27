package com.imooc.grocers.common;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResult doError(HttpServletRequest servletRequest,
                                HttpServletResponse servletResponse,
                                Exception exception) {

        if (exception instanceof BusinessException) {
            return CommonResult.create(((BusinessException) exception).getCommonError(), "fail");
        } else if (exception instanceof NoHandlerFoundException) {
            CommonError commonError = new CommonError(EmBusinessError.NO_HANDLER_FOUND);
            return CommonResult.create(commonError, "fail");
        } else if (exception instanceof ServletRequestBindingException){
            CommonError commonError = new CommonError(EmBusinessError.BIND_EXCEPTION_ERROR);
            return CommonResult.create(commonError, "fail");
        } else {
            CommonError commonError = new CommonError(EmBusinessError.UNKNOWN_ERROR);
            return CommonResult.create(commonError, "fail");
        }

    }
}
