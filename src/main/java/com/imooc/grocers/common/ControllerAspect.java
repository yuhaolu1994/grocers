package com.imooc.grocers.common;

import com.imooc.grocers.controller.admin.AdminController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Configuration
public class ControllerAspect {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Around("execution(* com.imooc.grocers.controller.admin.*.*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object adminControllerBeforeValidation(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        AdminPermission adminPermission = method.getAnnotation(AdminPermission.class);

        if (Objects.isNull(adminPermission)) {
            return joinPoint.proceed();
        }

        String email = (String) httpServletRequest.getSession().getAttribute(AdminController.CURRENT_ADMIN_SESSION);

        if (Objects.isNull(email)) {
            if ("text/html".equals(adminPermission.produceType())) {
                httpServletResponse.sendRedirect("/admin/admin/loginpage");
                return null;
            } else {
                CommonError commonError = new CommonError(EmBusinessError.ADMIN_SHOULD_LOGIN);
                return CommonResult.create(commonError, "fail");
            }
        } else {
            return joinPoint.proceed();
        }
    }

}
