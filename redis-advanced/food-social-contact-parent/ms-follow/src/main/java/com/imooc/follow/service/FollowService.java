package com.imooc.follow.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisConstant;
import com.imooc.commons.exception.ParameterException;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.vo.SignInDinerInfo;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.commons.utils.DinerUtil;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.commons.utils.TokenUtils;
import com.imooc.diners.client.DinersClient;
import com.imooc.diners.dto.LoginDinerInfo;
import com.imooc.follow.entity.Follow;
import com.imooc.follow.mapper.FollowMapper;
import com.imooc.oauth2.client.Oauth2ServerClient;
import org.assertj.core.util.Lists;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author E.T
 * @date 2023/4/16
 */
@Service
public class FollowService {
    @Resource
    private HttpServletRequest request;
    @Resource
    private Oauth2ServerClient oauth2ServerClient;
    @Resource
    private DinersClient dinersClient;
    @Resource
    private FollowMapper followMapper;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 关注/取消关注
     * @param followDinerId 被关注的用户id
     * @param isFollowed    是否关注, 0: 取消关注, 1: 关注
     * @return
     */
    public ResultInfo follow(Integer followDinerId, int isFollowed) {
        AssertUtil.isTrue(followDinerId == null, "关注的用户不能为空");
        String token = TokenUtils.parseToken(request);
        ResultInfo resultInfo = oauth2ServerClient.currentUser(token);
        System.err.println("resultInfo = " + resultInfo);
        if (resultInfo == null || resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            return ResultInfoUtil.buildError("", "获取当前用户信息失败");
        }
        SignInDinerInfo signInDinerInfo = DinerUtil.transToSignInDinerInfo(resultInfo);
        // 查询 DB 中是否存在关注信息。
        Follow follow = followMapper.selectFollow(signInDinerInfo.getId(), followDinerId);
        if (follow == null) {
            // 不存在关注信息，且 isFollowed 为 1，表示关注
            if (isFollowed == 1) {
                follow = new Follow();
                follow.setDinerId(signInDinerInfo.getId());
                follow.setFollowDinerId(followDinerId);
                int count = followMapper.save(follow);
                if (count == 1) {
                    addToRedisSet(follow.getDinerId(), followDinerId);
                }
                return ResultInfoUtil.buildSuccess("", "关注成功");
            }
        } else {
            // 存在关注信息，且 isFollowed 为 0，表示取消关注
            if (isFollowed == 0) {
                int count = followMapper.delete(follow.getId());
                if (count == 1) {
                    removeFromRedisSet(follow.getDinerId(), followDinerId);
                }
                return ResultInfoUtil.buildSuccess("", "取消关注成功");
            }
        }
        return ResultInfoUtil.buildSuccess("", "操作成功");
    }

    private void removeFromRedisSet(Integer dinerId, Integer followDinerId) {
        redisTemplate.opsForSet().remove(RedisConstant.FOLLOWING + dinerId, followDinerId);
        redisTemplate.opsForSet().remove(RedisConstant.FOLLOWERS + followDinerId, dinerId);
    }

    private void addToRedisSet(Integer dinerId, Integer followDinerId) {
        // 关注者的关注列表。
        redisTemplate.opsForSet().add(RedisConstant.FOLLOWING + dinerId, followDinerId);
        // 被关注着的粉丝列表。
        redisTemplate.opsForSet().add(RedisConstant.FOLLOWERS + followDinerId, dinerId);
    }

    public List<LoginDinerInfo> friends(Integer dinerId) {
        // 进行对比的用户是否存在关注列表。
        String targetUserFollowingKey = RedisConstant.FOLLOWING + dinerId;
        Boolean hasFollowing = redisTemplate.hasKey(RedisConstant.FOLLOWING + dinerId);
        if (hasFollowing == null || !hasFollowing) {
            return Lists.emptyList();
        }
        // 当前用户关注列表。
        String token = TokenUtils.parseToken(request);
        ResultInfo resultInfo = oauth2ServerClient.currentUser(token);
        if (resultInfo == null || resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            throw new ParameterException("获取当前用户信息失败");
        }
        SignInDinerInfo signInDinerInfo = DinerUtil.transToSignInDinerInfo(resultInfo);
        String currentUserFollowingKey = RedisConstant.FOLLOWING + signInDinerInfo.getId();
        Set<Integer> intersect = redisTemplate.opsForSet().intersect(currentUserFollowingKey, targetUserFollowingKey);
        // 没有共同关注的人。
        if (intersect == null || intersect.isEmpty()) {
            return Lists.emptyList();
        }
        String ids = StrUtil.join(",", intersect);
        System.err.println("ids = " + ids);
        ResultInfo dinersInfo = dinersClient.list(ids);
        if (dinersInfo == null || dinersInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            if (dinersInfo != null) {
                throw new ParameterException(dinersInfo.getMessage());
            } else {
                throw new ParameterException("无法查询到该用户信息");
            }
        }
        ArrayList<LinkedHashMap> list = (ArrayList<LinkedHashMap>) dinersInfo.getData();
        List<LoginDinerInfo> diners = list.stream().map(diner -> BeanUtil.fillBeanWithMap(diner, new LoginDinerInfo(), true))
                .collect(Collectors.toList());
        return diners;
    }

    public Set<Integer> followers(Integer dinerId) {
        String key = RedisConstant.FOLLOWERS + dinerId;
        return redisTemplate.opsForSet().members(key);
    }
}
