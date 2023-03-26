package com.imooc.oauth.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.domain.SignInIdentity;
import com.imooc.commons.model.vo.SignInDinerInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    RedisTokenStore redisTokenStore;

    @GetMapping(value = "/me")
    public ResultInfo currentUser(Authentication authentication) {
        SignInIdentity user = (SignInIdentity) authentication.getPrincipal();
        SignInDinerInfo userVO = new SignInDinerInfo();
        BeanUtils.copyProperties(user, userVO);
        return ResultInfoUtil.buildDataSuccess(userVO);
    }

    @GetMapping(value = "/logout")
    public ResultInfo logout(@RequestParam(name = "access_token", required = false) String accessToken,
                             String authorization) {

        log.info("accessToken = {}, authorization = {}", accessToken, authorization);
        accessToken = StringUtils.isBlank(accessToken) ? parseAuthorization(authorization) : accessToken;
        log.info("accessToken {} will be removed.", accessToken);

        OAuth2AccessToken oAuth2AccessToken = redisTokenStore.readAccessToken(accessToken);
        if (oAuth2AccessToken != null) {
            redisTokenStore.removeAccessToken(accessToken);
            // TODO:  无法通过直接传递 accessToken 的方式删除 refresh token. 怀疑与自定义前缀有关。
//            redisTokenStore.removeRefreshToken(accessToken);
            redisTokenStore.removeRefreshToken(oAuth2AccessToken.getRefreshToken());
        }
        return ResultInfoUtil.buildSuccess("");
    }

    private String parseAuthorization(String authorization) {
        if (StringUtils.isBlank(authorization)) {
            return null;
        } else if (authorization.contains("Bearer ")) {
            return authorization.replace("Bearer ", "");
        }
        return authorization;
    }
}
