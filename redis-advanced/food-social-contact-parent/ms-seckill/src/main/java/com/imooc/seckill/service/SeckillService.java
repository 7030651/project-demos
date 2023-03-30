package com.imooc.seckill.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisConstant;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.pojo.SeckillVouchers;
import com.imooc.commons.model.pojo.VoucherOrders;
import com.imooc.commons.model.vo.SignInDinerInfo;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.seckill.mapper.SeckillVouchersMapper;
import com.imooc.seckill.mapper.VoucherOrdersMapper;
import com.imooc.seckill.tools.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀业务逻辑层
 */
@Service
@Slf4j
public class SeckillService {

    @Resource
    private SeckillVouchersMapper seckillVouchersMapper;
    @Resource
    private VoucherOrdersMapper voucherOrdersMapper;
    @Value("${service.name.ms-oauth-server}")
    private String oauthServerName;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private DefaultRedisScript stockScript;
    @Resource
    private RedisLock redisLock;
    @Resource
    private RedissonClient redissonClient;

    private static final String KEY_AMOUNT = "amount";
    /**
     * 抢购代金券
     * - 基于 MySQL 的实现，会出现超卖及单人抢购多张代金券的情况。
     * - 解决方案：
     *  - 修改 SQL, 扣库存时增加 amount > 0 的条件。 (解决超卖)
     *  - 对用户的行为进行加锁。 (解决单人抢购多张代金券)
     *      - 查询用户代金券时增加读锁 - for update （需关闭自动提交事务）
     *      - 使用分布式锁，如 redis . √
     * @param voucherId   代金券 ID
     * @param accessToken 登录token
     * @Para path 访问路径
     */
    public ResultInfo doSeckillWithMysql(Integer voucherId, String accessToken) {
        SeckillVouchers seckillVouchers = seckillVouchersMapper.selectVoucher(voucherId);
        checkVoucherValid(seckillVouchers);

        ResultInfo resultInfo = getLoginUserInfo(accessToken);
        if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            return resultInfo;
        }
        // 这里的data是一个LinkedHashMap，SignInDinerInfo
        SignInDinerInfo dinerInfo = transToSignInDinerInfo(((LinkedHashMap) resultInfo.getData()));
        // 判断登录用户是否已抢到(一个用户针对这次活动只能买一次)
        VoucherOrders order = voucherOrdersMapper.findDinerOrder(dinerInfo.getId(),
                seckillVouchers.getFkVoucherId());
        AssertUtil.isTrue(order != null, "该用户已抢到该代金券，无需再抢");

        // 扣库存
        int count = seckillVouchersMapper.stockDecrease(seckillVouchers.getId());
        AssertUtil.isTrue(count == 0, "该券已经卖完了");
        VoucherOrders voucherOrders = new VoucherOrders(seckillVouchers, dinerInfo);
        count = voucherOrdersMapper.save(voucherOrders);
        AssertUtil.isTrue(count == 0, "用户抢购失败");
        return ResultInfoUtil.buildSuccess("抢购成功");
    }

//    @Transactional(rollbackFor = Exception.class)
    public ResultInfo doSeckill(Integer voucherId, String accessToken) {

        String key = RedisConstant.SECKILL_VOUCHERS + voucherId;
        // 在抢购活动开始前，优惠券相关信息应以被存入 redis 中，此处直接从 redis 中获取。
        Map<String, Object> map = redisTemplate.opsForHash().entries(key);
        SeckillVouchers seckillVouchers = BeanUtil.mapToBean(map, SeckillVouchers.class, true, null);

        // 2. 判断抢购是否开始、结束
        checkVoucherValid(seckillVouchers);
        // 获取登录用户信息
        ResultInfo resultInfo = getLoginUserInfo(accessToken);
        if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            return resultInfo;
        }
        // 这里的data是一个LinkedHashMap，SignInDinerInfo
        SignInDinerInfo dinerInfo = transToSignInDinerInfo((LinkedHashMap) resultInfo.getData());
        // 判断登录用户是否已抢到(一个用户针对这次活动只能买一次)
        VoucherOrders order = voucherOrdersMapper.findDinerOrder(
                                dinerInfo.getId(), seckillVouchers.getFkVoucherId());
        AssertUtil.isTrue(order != null, "该用户已抢到该代金券，无需再抢");

        // 使用分布式锁，解决单人可抢购多次的问题。
        String lockKey = String.join(RedisConstant.DELIMITER, RedisConstant.LOCK_KEY, dinerInfo.getId().toString(), voucherId.toString());
        // 使用自定义 redis 分布式锁。
        // String redisKey = redisLock.tryLock(lockKey, RedisConstant.DEFAULT_EXPIRE_TIMES);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 已获得锁。
            // if (redisKey != null) { // 自定义 redis 分布式锁
            boolean isLocked = lock.tryLock(RedisConstant.DEFAULT_EXPIRE_TIMES, TimeUnit.SECONDS);
            if (isLocked) {
            // 扣库存。
                /**
                 * 仅使用 redis 时，redis 中的数据会出现超卖现象。
                 * long count = redisTemplate.opsForHash().increment(key, KEY_AMOUNT, -1);
                 * AssertUtil.isTrue(count < 0, "该券已经卖完了");
                 */
                // 使用 redis + lua，解决超卖的问题。
                Long amount = (Long) redisTemplate.execute(stockScript, List.of(key, KEY_AMOUNT));
                // amount 的值为未本次抢购未扣库存之前的值，所以此处与 1 做比较。
                AssertUtil.isTrue(amount == null || amount.longValue() < 1, "该券已抢完。");
                // 下单
                /*
                    TODO: 当操作数据库出现异常时，redis 中已经扣减的库存无法回滚。解决方案：
                    1. 先存数据库，再存 redis。
                    2. 选用其他数据一致性存储方案。
                 */
                VoucherOrders voucherOrders = new VoucherOrders(seckillVouchers, dinerInfo);
                long count = voucherOrdersMapper.save(voucherOrders);
                AssertUtil.isTrue(count == 0, "用户抢购失败");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            /*
                自定义分布式锁实现。
                if (redisKey != null) {
                redisLock.unlock(lockKey, redisKey);
             */
            lock.unlock();
        }

        return ResultInfoUtil.buildSuccess("抢购成功");
    }

    /**
     * 添加需要抢购的代金券
     *
     * @param seckillVouchers
     */
    @Transactional(rollbackFor = Exception.class)
    public void addSeckillVouchers(SeckillVouchers seckillVouchers) {
        // 非空校验
        AssertUtil.isTrue(seckillVouchers.getFkVoucherId() == null, "请选择需要抢购的代金券");
        AssertUtil.isTrue(seckillVouchers.getAmount() == 0, "请输入抢购总数量");
        Date now = new Date();
        // 生产环境下面一行代码需放行，这里注释方便测试
        // AssertUtil.isTrue(now.after(seckillVouchers.getStartTime()), "开始时间不能早于当前时间");
        AssertUtil.isTrue(now.after(seckillVouchers.getEndTime()), "结束时间不能早于当前时间");
        AssertUtil.isTrue(seckillVouchers.getStartTime().after(seckillVouchers.getEndTime()), "开始时间不能晚于结束时间");

        /*
            采用 MySQL 实现。
            // 验证数据库中是否已经存在该券的秒杀活动
            SeckillVouchers seckillVouchersFromDb = seckillVouchersMapper.selectVoucher(seckillVouchers.getFkVoucherId());
            AssertUtil.isTrue(seckillVouchersFromDb != null, "该券已经拥有了抢购活动");
            // 插入数据库
            seckillVouchersMapper.save(seckillVouchers);
         */

        // 采用 Redis 实现
        String key = RedisConstant.SECKILL_VOUCHERS + seckillVouchers.getFkVoucherId();
        // 验证 Redis 中是否已经存在该券的秒杀活动
        Map<String, Object> map = redisTemplate.opsForHash().entries(key);
        AssertUtil.isTrue(!map.isEmpty() && (int) map.get(KEY_AMOUNT) > 0, "该券已经拥有了抢购活动");

        // 插入 Redis
        seckillVouchers.setIsValid(1);
        seckillVouchers.setCreateDate(now);
        seckillVouchers.setUpdateDate(now);
        redisTemplate.opsForHash().putAll(key, BeanUtil.beanToMap(seckillVouchers));
    }

    private SignInDinerInfo transToSignInDinerInfo(LinkedHashMap data) {
        SignInDinerInfo dinerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) data,
                new SignInDinerInfo(), false);

        return dinerInfo;
    }

    private ResultInfo getLoginUserInfo(String accessToken) {
        // 获取登录用户信息
        String url = oauthServerName + "user/me?access_token={accessToken}";
        ResultInfo resultInfo = restTemplate.getForObject(url, ResultInfo.class, accessToken);
        return resultInfo;
    }


    /**
     * 判断是否能够抢购。
     * @param seckillVouchers
     */
    private void checkVoucherValid(SeckillVouchers seckillVouchers) {
        // 1. 判断此代金券是否加入抢购
        AssertUtil.isTrue(seckillVouchers == null, "该代金券并未有抢购活动");
        // 判断是否有效
        AssertUtil.isTrue(seckillVouchers.getIsValid() == 0, "该活动已结束");

        // 2. 判断抢购是否开始、结束
        Date now = new Date();
        AssertUtil.isTrue(now.before(seckillVouchers.getStartTime()), "该抢购还未开始");
        AssertUtil.isTrue(now.after(seckillVouchers.getEndTime()), "该抢购已结束");
        // 3. 判断是否卖完
        AssertUtil.isTrue(seckillVouchers.getAmount() < 1, "该券已经卖完了");
    }

}