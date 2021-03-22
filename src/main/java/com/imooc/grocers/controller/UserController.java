package com.imooc.grocers.controller;

import com.imooc.grocers.common.BusinessException;
import com.imooc.grocers.common.CommonResult;
import com.imooc.grocers.common.CommonUtil;
import com.imooc.grocers.common.EmBusinessError;
import com.imooc.grocers.model.UserModel;
import com.imooc.grocers.request.LoginReq;
import com.imooc.grocers.request.RegisterReq;
import com.imooc.grocers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Controller("/user")
@RequestMapping("/user")
public class UserController {

    public static final String CURRENT_USER_SESSION = "currentUserSession";

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private UserService userService;

    @RequestMapping("/get")
    @ResponseBody
    public CommonResult getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        UserModel userModel = userService.getUser(id);
        if (Objects.isNull(userModel)) {
            /**
             * version 1: use error code and error msg
             */
            // return CommonResult.create(new CommonError(EmBusinessError.NO_OBJECT_FOUND), "fail");
            /**
             * version 2: use exception AOP
             */
            throw new BusinessException(EmBusinessError.NO_OBJECT_FOUND);
        } else {
            return CommonResult.create(userModel);
        }
    }

    @RequestMapping("/register")
    @ResponseBody
    public CommonResult register(@Valid @RequestBody RegisterReq registerReq, BindingResult bindingResult) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processErrorString(bindingResult));
        }

        UserModel registerUser = new UserModel();
        registerUser.setTelephone(registerReq.getTelephone());
        registerUser.setPassword(registerReq.getPassword());
        registerUser.setNickName(registerReq.getNickName());
        registerUser.setGender(registerReq.getGender());

        UserModel resUserModel = userService.register(registerUser);

        return CommonResult.create(resUserModel);
    }

    @RequestMapping("/login")
    @ResponseBody
    public CommonResult login(@RequestBody @Valid LoginReq loginReq, BindingResult bindingResult) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processErrorString(bindingResult));
        }

        UserModel userModel = userService.login(loginReq.getTelephone(), loginReq.getPassword());
        httpServletRequest.getSession().setAttribute(CURRENT_USER_SESSION, userModel); // userModel should implement Serializable

        return CommonResult.create(userModel);
    }

    @RequestMapping("/logout")
    @ResponseBody
    public CommonResult logout() {
        httpServletRequest.getSession().invalidate();
        return CommonResult.create(null);
    }

    @RequestMapping("/getcurrentuser")
    @ResponseBody
    public CommonResult getCurrentUser() {
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute(CURRENT_USER_SESSION);
        return CommonResult.create(userModel);
    }

}
