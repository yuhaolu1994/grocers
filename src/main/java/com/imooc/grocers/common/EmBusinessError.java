package com.imooc.grocers.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum EmBusinessError {

    // 通用的错误类型10000开头
    NO_OBJECT_FOUND(10001, "请求对象不存在"),
    UNKNOWN_ERROR(10002, "未知错误"),
    NO_HANDLER_FOUND(10003, "找不到执行的路径操作"),
    BIND_EXCEPTION_ERROR(10004, "请求参数错误"),
    PARAMETER_VALIDATION_ERROR(10005, "请求参数校验失败"),

    // 用户服务相关的错误类型20000开头
    REGISTER_DUP_FAIL(20001, "用户已存在"),

    LOGIN_FAIL(20002, "手机号或密码错误"),

    //admin相关错误
    ADMIN_SHOULD_LOGIN(30001, "管理员需要先登录"),

    //品类相关错误
    CATEGORY_NAME_DUPLICATED(40001, "品类名已存在");

    @Getter
    @Setter
    private Integer errorCode;

    @Getter
    @Setter
    private String errorMsg;

}
