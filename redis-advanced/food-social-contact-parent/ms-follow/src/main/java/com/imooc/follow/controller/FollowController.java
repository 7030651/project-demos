package com.imooc.follow.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.diners.dto.LoginDinerInfo;
import com.imooc.follow.service.FollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author E.T
 * @date 2023/4/16
 */
@RestController
@RequestMapping(value = "/follow")
public class FollowController {
    @Resource
    HttpServletRequest request;

    @Resource
    private FollowService followService;

    @GetMapping(value = "/friends/{dinerId}")
    public ResultInfo friends(@PathVariable Integer dinerId) {
        List<LoginDinerInfo> friends = followService.friends(dinerId);
        return ResultInfoUtil.buildSuccess(request.getRequestURI(), friends);
    }
    @PostMapping(value = "/{followDinerId}")
    public ResultInfo follow(@PathVariable Integer followDinerId, int isFollowed) {
        return followService.follow(followDinerId, isFollowed);
    }
}
