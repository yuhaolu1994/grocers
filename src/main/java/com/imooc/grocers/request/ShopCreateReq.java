package com.imooc.grocers.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class ShopCreateReq {

    @NotBlank(message = "empty shop name found")
    private String name;

    @NotNull(message = "null pricePerMan found")
    private Integer pricePerMan;

    @NotNull(message = "null latitude found")
    private BigDecimal latitude;

    @NotNull(message = "null longitude found")
    private BigDecimal longitude;

    @NotNull(message = "null categoryId found")
    private Integer categoryId;

    private String tags;

    @NotBlank(message = "empty startTime found")
    private String startTime;

    @NotBlank(message = "empty endTime found")
    private String endTime;

    @NotBlank(message = "empty address found")
    private String address;

    @NotNull(message = "null sellerId found")
    private Integer sellerId;

    @NotBlank(message = "empty iconUrl found")
    private String iconUrl;

}
