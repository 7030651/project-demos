package com.imooc.commons.constant;

/**
 * redis 相关业务的常量。
 */
public interface RedisConstant {
    String DELIMITER = ";";
    long DEFAULT_EXPIRE_TIMES = 60;

    /** 短信验证码 */
    String VERIFY_CODE = "verify_code:";
    /** 秒杀券的 key */
    String SECKILL_VOUCHERS = "seckill_vouchers:";
    /** 分布式锁的 Key */
    String LOCK_KEY = "lock_by";
    /** 关注集合的 KEY */
    String FOLLOWING = "following:";
    /** 粉丝集合的 KEY */
    String FOLLOWERS = "followers:";
    /** 关注的信息流的 KEY */
    String FOLLOWING_FEEDS = "following_feeds:";

    /**
     * 用户签到的 KEY 的格式化字符串。
     * %d: 用户 ID
     * %s: 日期，格式为 yyyyMM
     */
    String FORMAT_DINER_SIGN = "user:sign:%d:%s";

    /**
     * 构建用户签到的 KEY。
     * @see #FORMAT_DINER_SIGN
     * @param dinerId
     * @param date yyyyMM
     * @return
     */
    static String buildDinerSignKey(Integer dinerId, String date) {
        return String.format(FORMAT_DINER_SIGN, dinerId, date);
    }
}
