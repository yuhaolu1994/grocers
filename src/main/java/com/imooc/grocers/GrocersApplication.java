package com.imooc.grocers;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {"com.imooc.grocers"})
@MapperScan("com.imooc.grocers.dal")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class GrocersApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrocersApplication.class, args);
    }

}
