package com.imooc.follow.client;

import com.imooc.commons.model.domain.ResultInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author E.T
 * @date 2023/4/17
 */
@FeignClient(name = "ms-follow", path = "/follow")
public interface FollowClient {

    @GetMapping(value = "/{dinerId}/followers")
    public ResultInfo followers(@PathVariable(value = "dinerId") Integer dinerId);
}