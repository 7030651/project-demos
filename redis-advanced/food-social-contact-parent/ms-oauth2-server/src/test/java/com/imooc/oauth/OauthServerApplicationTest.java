package com.imooc.oauth;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

/**
 * @author E.T
 * @date 2023/3/29
 */

@SpringBootTest
@AutoConfigureMockMvc
public class OauthServerApplicationTest {
    @Resource
    protected MockMvc mockMvc;
}
