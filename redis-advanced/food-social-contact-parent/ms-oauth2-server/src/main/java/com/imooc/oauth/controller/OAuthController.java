package com.imooc.oauth.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OAuth2 控制器
 * 替换 spring security 提供的 /oauth/token 接口，对返回结果进行定制。
 */
@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {

    @Resource
    private TokenEndpoint tokenEndpoint;

    @Resource
    private HttpServletRequest request;


    @GetMapping(value = "/test", params = {"a"} )
    public String get(String a) {
        return "test " + a;
    }


    @PostMapping("/token")
    public ResultInfo postAccessToken(Principal principal, @RequestParam Map<String, String> parameters)
            throws HttpRequestMethodNotSupportedException {
        System.err.println("parameters = " +  parameters);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            System.err.println("key = " + key + ", value = " + value);
        }
        return custom(tokenEndpoint.postAccessToken(principal, parameters).getBody());
    }

    /**
     * 自定义 Token 返回对象
     *
     * @param accessToken
     * @return
     */
    private ResultInfo custom(OAuth2AccessToken accessToken) {
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        Map<String, Object> data = new LinkedHashMap<>(token.getAdditionalInformation());
        data.put("accessToken", token.getValue());
        data.put("expireIn", token.getExpiresIn());
        data.put("scopes", token.getScope());
        if (token.getRefreshToken() != null) {
            data.put("refreshToken", token.getRefreshToken().getValue());
        }
        return ResultInfoUtil.buildSuccess(request.getServletPath(), data);
    }

}