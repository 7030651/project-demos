package com.imooc.oauth2.client;

import com.imooc.commons.model.domain.ResultInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author E.T
 * @date 2023/4/16
 */
@FeignClient(value = "ms-oauth2-server")
public interface Oauth2ServerClient {
    @GetMapping(value = "/user/me")
    public ResultInfo currentUser(@RequestParam(name = "access_token", required = true) String accessToken);
}
