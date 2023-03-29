package com.imooc.commons.constant;

/**
 * redis 相关业务的常量。
 */
public interface RedisConstant {

    /** 短信验证码 */
    String VERIFY_CODE = "verify_code:";
    /** 秒杀券的 key */
    String SECKILL_VOUCHERS = "seckill_vouchers:";
    /** 分布式锁的 Key */
    String LOCK_KEY = "lockby:";
}
