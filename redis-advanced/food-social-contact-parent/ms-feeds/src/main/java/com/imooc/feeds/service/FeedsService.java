package com.imooc.feeds.service;

import cn.hutool.core.date.DateTime;
import com.imooc.commons.constant.RedisConstant;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.TokenUtils;
import com.imooc.feeds.entity.Feeds;
import com.imooc.feeds.mapper.FeedsMapper;
import com.imooc.follow.client.FollowClient;
import com.imooc.oauth2.client.Oauth2ServerClient;
import com.imooc.oauth2.dto.SignInDinerResponse;
import org.apache.http.util.Asserts;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author E.T
 * @date 2023/4/19
 */
@Service
public class FeedsService {

    @Resource
    private FollowClient followClient;
    @Resource
    private Oauth2ServerClient oauth2ServerClient;
    @Resource
    private HttpServletRequest request;
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private FeedsMapper feedsMapper;

    @Transactional(rollbackFor = Exception.class)
    public Feeds create(Feeds feeds) {
        // 有效性校验
        // 获取当前用户信息
        String token = TokenUtils.parseToken(request);
        SignInDinerResponse diner = oauth2ServerClient.current();
        Assert.notNull(diner, "用户信息不存在");
        // 保存动态
        int count = feedsMapper.save(feeds);
        Assert.isTrue(count != 0, "动态保存失败，请稍后重试。");
        // 获取粉丝列表
        String key = RedisConstant.FOLLOWERS + diner.getData().getId();
        Set<Integer> members = redisTemplate.opsForSet().members(key);
        Assert.notNull(members, "无法获取粉丝列表，请稍后重试。");
        // 推送动态，若没有粉丝，则不会进行推送。
        long score = System.currentTimeMillis();
        members.stream().forEach(follower -> {
            String feedsKey = RedisConstant.FOLLOWING_FEEDS + follower;
            redisTemplate.opsForZSet().add(feedsKey, feeds.getId(), score);
        });

        return feeds;
    }
}
