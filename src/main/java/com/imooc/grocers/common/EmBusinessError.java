package com.imooc.grocers.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum EmBusinessError {

    // generic error starts with 10000
    NO_OBJECT_FOUND(10001, "Request object not found"),
    UNKNOWN_ERROR(10002, "Unknown error"),
    NO_HANDLER_FOUND(10003, "Handler not found for current path"),
    BIND_EXCEPTION_ERROR(10004, "Request parameters error"),
    PARAMETER_VALIDATION_ERROR(10005, "Request parameters validation error"),

    // user service error starts with 20000
    REGISTER_DUP_FAIL(20001, "User already exists"),

    LOGIN_FAIL(20002, "Phone num or password error"),

    // admin related error
    ADMIN_SHOULD_LOGIN(30001, "Admin login is needed"),

    // category related error
    CATEGORY_NAME_DUPLICATED(40001, "Category name already exists");

    @Getter
    @Setter
    private Integer errorCode;

    @Getter
    @Setter
    private String errorMsg;

}
