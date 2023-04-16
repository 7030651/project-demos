package com.imooc.follow.mapper;

import com.imooc.follow.entity.Follow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author E.T
 * @date 2023/4/16
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FollowMapperTest {

    @Resource
    private FollowMapper followMapper;

    @Test
    public void testFollowMapper() {
        System.out.println(followMapper.selectFollow(1, 2));
        Follow follow = new Follow().setDinerId(2).setFollowDinerId(1);
        followMapper.save(follow);
        System.out.println("follow: " + follow);
        System.out.println(followMapper.update(follow.getId(), 1));
    }
}
