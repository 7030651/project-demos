package com.imooc.diners.client;

import com.imooc.commons.model.domain.ResultInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author E.T
 * @date 2023/4/17
 */
@FeignClient(name = "ms-diners")
public interface DinersClient {

    @GetMapping(value = "/diners/list")
    public ResultInfo list(@RequestParam(name = "ids") String ids);
}
