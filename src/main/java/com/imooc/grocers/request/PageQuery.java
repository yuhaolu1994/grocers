package com.imooc.grocers.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageQuery {
    // 分页数据有默认值，不需要验证
    private Integer page = 1;

    private Integer size = 20;

}
