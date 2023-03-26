package com.imooc.diners.controller;

import com.imooc.commons.exception.ParameterException;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.diners.domain.Diners;
import com.imooc.diners.dto.DinerRegisterRequests;
import com.imooc.diners.service.DinersService;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/diners")
public class DinersController {

    @Resource
    private DinersService dinersService;
    @Resource
    private HttpServletRequest request;

    @GetMapping("/signin")
    public ResultInfo signIn(String account, String password) {
        ResultInfo resultInfo = dinersService.signIn(account, password);
        if (resultInfo != null) {
            resultInfo.setPath(request.getServletPath());
        }
        return resultInfo;
    }

    @PostMapping(value = "/register")
    public ResultInfo register(@Valid @RequestBody DinerRegisterRequests requests,
                               BindingResult errors) {
        if (errors.hasErrors()) {
            return ResultInfoUtil.buildError(errors);
        }
        System.err.println("request = " + requests);
        requests.setPassword(requests.getPassword().trim());
        Diners diners = dinersService.register(requests);
        AssertUtil.isNotNull(diners, "注册失败，请稍后重试。");
        // 注册成功后自动登录。
        return dinersService.signIn(requests.getUsername(),requests.getPassword());
    }
}
