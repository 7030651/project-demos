package com.imooc.follow.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.TokenUtils;
import com.imooc.oauth2.client.Oauth2ServerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author E.T
 * @date 2023/4/16
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private HttpServletRequest request;
    @Resource
    private Oauth2ServerClient oauth2ServerClient;

    @GetMapping(path = "/test")
    public String test(@RequestParam(name = "tts") String token) {
        String accessToken = TokenUtils.parseToken(request);
        System.out.println("param is : " + token + ", token is : " + accessToken);
        ResultInfo resultInfo = oauth2ServerClient.currentUser(accessToken);
        System.out.printf("resultInfo :" + resultInfo);
        return "token is : " + token;
    }
}
