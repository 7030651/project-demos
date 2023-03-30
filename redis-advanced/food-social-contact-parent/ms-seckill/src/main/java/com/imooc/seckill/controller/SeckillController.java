package com.imooc.seckill.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.pojo.SeckillVouchers;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.commons.utils.TokenUtils;
import com.imooc.seckill.service.SeckillService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 秒杀控制层
 */
@RestController
@RequestMapping(value = "/seckill")
public class SeckillController {

    @Resource
    private SeckillService seckillService;
    @Resource
    private HttpServletRequest request;

    /**
     * 秒杀下单
     *3
     * @param voucherId
     * @return
     */
    @GetMapping(value = "/{voucherId}")
    public ResultInfo doSeckill(@PathVariable Integer voucherId) {
        String accessToken = TokenUtils.parseToken(request);
//        ResultInfo resultInfo = seckillService.doSeckillWithMysql(voucherId, accessToken);
        ResultInfo resultInfo = seckillService.doSeckill(voucherId, accessToken);
        if (resultInfo != null) {
            resultInfo.setPath(request.getServletPath());
        }
        return resultInfo;
    }

    /**
     * 新增秒杀活动
     *
     * @param seckillVouchers
     * @return
     */
    @PostMapping(value = "/add")
    public ResultInfo addSeckillVouchers(@RequestBody SeckillVouchers seckillVouchers) {
        seckillService.addSeckillVouchers(seckillVouchers);
        return ResultInfoUtil.buildSuccess(request.getServletPath());
    }

}