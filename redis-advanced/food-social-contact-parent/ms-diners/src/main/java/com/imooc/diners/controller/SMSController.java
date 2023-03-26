package com.imooc.diners.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.diners.service.SMSService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 短信服务
 */
@RestController
@RequestMapping(value = "/sms")
public class SMSController {

    @Resource
    SMSService smsService;
    @Resource
    private HttpServletRequest request;

    /**
     * @author E.T
     * @date 2022/12/4
     */
    @GetMapping(value = "/send")
    public ResultInfo send(String phoneNumber) {
        AssertUtil.isNotEmpty(phoneNumber, "phoneNumber must not be null.");
        boolean send = smsService.send(phoneNumber);
        if (!send) {
            return ResultInfoUtil.buildError("", "系统繁忙，请稍后重试。");
        }
        return ResultInfoUtil.buildSuccess(request.getServletPath());
    }
}
