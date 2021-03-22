package com.imooc.grocers.controller.admin;

import com.imooc.grocers.common.BusinessException;
import com.imooc.grocers.common.CommonResult;
import com.imooc.grocers.common.EmBusinessError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Controller("/admin/admin")
@RequestMapping("/admin/admin")
public class AdminController {

    @Value("${admin.email}")
    private String email;

    @Value("${admin.encryptPassword}")
    private String encryptPassword;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public static final String CURRENT_ADMIN_SESSION = "currentAdminSession";

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestParam(name = "email") String email,
                              @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "Empty email or password found");
        }
        if (email.equals(this.email) && encodeByMd5(password).equals(this.encryptPassword)) {
            httpServletRequest.getSession().setAttribute(CURRENT_ADMIN_SESSION, email);
            return CommonResult.create(email);
        } else {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "Invalid email or password found");
        }
    }

    private String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();

        return base64Encoder.encode(messageDigest.digest(str.getBytes("utf-8")));
    }
}
