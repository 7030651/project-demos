package com.imooc.diners.service;

import cn.hutool.core.bean.BeanUtil;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.diners.config.OAuth2ClientConfigProperties;
import com.imooc.diners.domain.Diners;
import com.imooc.diners.domain.OAuthDinerInfo;
import com.imooc.diners.dto.DinerRegisterRequests;
import com.imooc.diners.dto.LoginDinerInfo;
import com.imooc.diners.mapper.DinersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@Slf4j
public class DinersService {

    @Resource
    DinersMapper dinersMapper;
    @Resource
    SMSService smsService;
    @Resource
    private RestTemplate restTemplate;
    @Value("${service.name.ms-oauth-server}")
    private String oauthServerName;
    @Resource
    private OAuth2ClientConfigProperties oAuth2ClientConfigProperties;

    public List<LoginDinerInfo> list(Integer[] ids) {
        return dinersMapper.findByIds(ids);
    }
    public ResultInfo signIn(String account, String password) {
        // 构造 rest 请求。
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.set("username", account);
        body.set("password", password);
        body.set("grant_type", oAuth2ClientConfigProperties.getGrantType());
        body.setAll(BeanUtil.beanToMap(oAuth2ClientConfigProperties));

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor(oAuth2ClientConfigProperties.getClientId()
                        , oAuth2ClientConfigProperties.getSecret()));
        ResponseEntity<ResultInfo> response = restTemplate.postForEntity(
                oauthServerName + "oauth/token", entity, ResultInfo.class);

        // 处理返回结果
        AssertUtil.isTrue(response.getStatusCode() != HttpStatus.OK, "登录失败");
        ResultInfo resultInfo = response.getBody();

        if (resultInfo.getCode() != null && resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            // 登录失败
            resultInfo.setData(resultInfo.getMessage());
            return resultInfo;
        }
        // 这里的 Data 是一个 LinkedHashMap 转成了域对象 OAuthDinerInfo
        OAuthDinerInfo dinerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) resultInfo.getData(),
                new OAuthDinerInfo(), false);
        // 根据业务需求返回视图对象
        LoginDinerInfo loginDinerInfo = new LoginDinerInfo();
        loginDinerInfo.setToken(dinerInfo.getAccessToken());
        loginDinerInfo.setAvatarUrl(dinerInfo.getAvatarUrl());
        loginDinerInfo.setNickname(dinerInfo.getNickname());
        return ResultInfoUtil.buildSuccess(null, loginDinerInfo);
    }

    /**
     * 用户注册
     * @param requests
     * @return
     */
    public Diners register(DinerRegisterRequests requests) {
        String code = smsService.getCodeByPhone(requests.getPhone());
        AssertUtil.isNotEmpty(code, "验证码已过期");
        AssertUtil.isTrue(!code.equals(requests.getVerifyCode()), "验证码错误，请重新输入。");
        Diners entity = dinersMapper.selectByPhone(requests.getPhone());
        AssertUtil.isTrue(entity != null, "用户已存在。");
        entity = new Diners();
        // TODO use MapStruct.
        BeanUtils.copyProperties(requests, entity);
        System.err.println("entity = " + entity);
        entity.setPassword(DigestUtils.md5DigestAsHex(entity.getPassword().getBytes()));
        dinersMapper.save(entity);
        entity.setPassword(null);
        return entity;
    }
}
