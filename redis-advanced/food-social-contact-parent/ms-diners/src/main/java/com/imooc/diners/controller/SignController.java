package com.imooc.diners.controller;

import com.imooc.commons.model.BaseResponse;
import com.imooc.diners.dto.DinerSignRespVO;
import com.imooc.diners.dto.DinerSignResponse;
import com.imooc.diners.service.SignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * @author E.T
 * @date 2022/12/5
 */

@RestController
@RequestMapping(path = "/sign")
public class SignController {

    @Resource
    private SignService signService;

    /**
     * 签到接口
     * @param date
     * @return
     */
    @PostMapping
    public BaseResponse sign(@RequestParam(required = false) String date) {
        signService.sign(date);
        return BaseResponse.success();
    }

    /**
     * 获取签到天数
     * @param date 签到的月份，默认为当月。
     * @return
     */
    @GetMapping(path = "/days" )
    public DinerSignResponse days(@RequestParam(required = false) String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        YearMonth yearMonth = StringUtils.isNotBlank(date) ?
                YearMonth.parse(date, formatter) : YearMonth.now();
        date = yearMonth.format(formatter);
        return new DinerSignResponse(signService.days(date));
    }

    @GetMapping(path = "/test")
    public Object test(@RequestParam Integer dinerId, @RequestParam(required = false) String date) {
        if (StringUtils.isBlank(date)) {
            date = "20230420";
        }
        return signService.continuousSignDays(dinerId, date);
    }
}
