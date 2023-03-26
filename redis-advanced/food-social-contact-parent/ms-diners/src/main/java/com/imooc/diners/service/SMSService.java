package com.imooc.diners.service;

import cn.hutool.core.util.RandomUtil;
import com.imooc.commons.constant.RedisConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 短信验证服务
 */
@Service
public class SMSService {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    /**
     * 发送验证码至用户手机，并将验证码存入 redis.
     * @param phoneNumber   用户手机号码
     * @return
     */
    public boolean send(String phoneNumber) {
        String code = RandomUtil.randomNumbers(6);
        if (!isValid(phoneNumber)) {
            return false;
        }
        stringRedisTemplate.opsForValue().set(
                RedisConstant.VERIFY_CODE.concat(phoneNumber), code, 60, TimeUnit.SECONDS);
        return true;
    }

    private boolean isValid(String phoneNumber) {
        // redis 中是否已存在手机号对应的验证码
        String verifyCode = stringRedisTemplate.opsForValue()
                .get(RedisConstant.VERIFY_CODE.concat(phoneNumber));
        // redis 中不存在手机号对应的验证码时，视为有效的手机号。
        return StringUtils.isBlank(verifyCode);
    }

    /**
     * 根据手机号获取验证码
     *
     * @param phone
     * @return
     */
    public String getCodeByPhone(String phone) {
        String key = RedisConstant.VERIFY_CODE.concat(phone);
        return stringRedisTemplate.opsForValue().get(key);
    }
}
