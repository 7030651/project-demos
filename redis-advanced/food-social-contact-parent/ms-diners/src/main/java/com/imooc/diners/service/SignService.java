package com.imooc.diners.service;

import com.imooc.commons.constant.RedisConstant;
import com.imooc.commons.model.vo.SignInDinerInfo;
import com.imooc.diners.dto.DinerSignRespVO;
import com.imooc.oauth2.client.Oauth2ServerClient;
import com.imooc.oauth2.dto.SignInDinerResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author E.T
 * @date 2022/12/5
 */
@Service
public class SignService {
    @Resource
    private Oauth2ServerClient oauth2ServerClient;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 用户签到
     * @param dateStr 需要签到的日期，该值仅对格式进行校验，如果为空，则默认为当天。
     * @return
     */
    public boolean sign(String dateStr) {
        LocalDate date = StringUtils.isNotBlank(dateStr) ?
                LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE) : LocalDate.now();
         SignInDinerInfo diner = getDiner();

        // 获取签到日期对应当月的天数。
        int offset = date.getDayOfMonth() - 1;  // bitmap 下标从 0 开始计数。
        // eg: user:sign:1:202304
        String signKey = RedisConstant.buildDinerSignKey(diner.getId(), date.format(DateTimeFormatter.ofPattern("yyyyMM")));
        Boolean isSigned = redisTemplate.opsForValue().getBit(signKey, offset);
        Assert.isTrue(Boolean.FALSE.equals(isSigned), "今天已经签到过了");

        // 签到
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setBit(signKey, offset, true));

    }

    /**
     * 某月的连续签到天数。
     * @param date
     * @return
     */
    public DinerSignRespVO days(String date) {
        SignInDinerInfo diner = getDiner();
        String signKey = RedisConstant.buildDinerSignKey(diner.getId(), date);
        Long count = (Long) redisTemplate.execute(
                (RedisCallback<Long>) conn -> conn.bitCount(signKey.getBytes()));
        Assert.isTrue(count != null, "无法获取签到数据，请稍后重试。");
        return new DinerSignRespVO(date, count);
    }

    /**
     * 获取连续签到天数
     * @param date yyyyMMdd
     * @return
     */
    public int continuousSignDays(Integer dinerId, String date) {

        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);
        int dayOfMonth = localDate.getDayOfMonth();

        // 构建 BitFieldCommands
        String key = RedisConstant.buildDinerSignKey(dinerId, localDate.format(DateTimeFormatter.ofPattern("yyyyMM")));
        BitFieldSubCommands bitFieldSubCommands = BitFieldSubCommands.create()
                .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
                .valueAt(0);
        List<Long> list = redisTemplate.opsForValue().bitField(key, bitFieldSubCommands);
        Assert.isTrue( list != null && list.size() == 1, "无法获取签到数据，请稍后重试。");
        // BitFieldCommands 的返回值。
        long result = list.get(0);

        // 从右向左进行位运算，当不为 1 时，即为签到中断。
        int signCount = 0;
        for (int i = dayOfMonth; i > 0; i --) {
            if ((result & 1) == 1) {
                signCount ++;
                result >>>= 1;
            } else {
                break;
            }
        }
        return signCount;
    }

    private SignInDinerInfo getDiner() {
        SignInDinerResponse current = oauth2ServerClient.current();
        return current.getData();
    }
}
