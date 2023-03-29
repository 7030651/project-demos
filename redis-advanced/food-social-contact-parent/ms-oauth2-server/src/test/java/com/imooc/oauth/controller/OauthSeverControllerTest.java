package com.imooc.oauth.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.oauth.OauthServerApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Base64Utils;

import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * @author E.T
 * @date 2023/3/29
 */
public class OauthSeverControllerTest extends OauthServerApplicationTest {
    @Test
    public void writeToken() throws Exception {
        String authorization = Base64Utils.encodeToString("appId:123456".getBytes());
        StringBuffer tokens = new StringBuffer();
        for (int i = 0; i < 2000; i++) {
            MvcResult mvcResult = super.mockMvc.perform(MockMvcRequestBuilders.post("/oauth/token")
                            .header("Authorization", "Basic " + authorization)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "test" + i)
                            .param("password", "123456")
                            .param("grant_type", "password")
                            .param("scope", "api")
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    // .andDo(MockMvcResultHandlers.print())
                    .andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            ResultInfo resultInfo = (ResultInfo) JSONUtil.toBean(contentAsString, ResultInfo.class);
            JSONObject result = (JSONObject) resultInfo.getData();
            String token = result.getStr("accessToken");
            tokens.append(token).append("\r\n");
        }

        Files.write(Paths.get("tokens.txt"), tokens.toString().getBytes());
    }
}
