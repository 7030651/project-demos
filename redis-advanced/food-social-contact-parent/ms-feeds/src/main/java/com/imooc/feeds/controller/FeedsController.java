package com.imooc.feeds.controller;

import com.imooc.commons.auth.FeignRequestHeaderInterceptor;
import com.imooc.commons.exception.ParameterException;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.feeds.dto.FeedsResponse;
import com.imooc.feeds.entity.Feeds;
import com.imooc.feeds.service.FeedsService;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author E.T
 * @date 2023/4/19
 */
@RestController
@RequestMapping(value = "/feeds")
public class FeedsController {

    @Resource
    private FeedsService feedsService;

    @PostMapping(value = "/create")
    public FeedsResponse create(@RequestBody @Validated Feeds feeds) {
        Feeds result = feedsService.create(feeds);
        return new FeedsResponse(result);
    }
}
