package com.imooc.commons.auth;

import feign.RequestInterceptor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

/**
 * @author E.T
 * @date 2023/4/19
 */
public class FeignRequestHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(feign.RequestTemplate requestTemplate) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            String token = servletRequestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            requestTemplate.header(HttpHeaders.AUTHORIZATION, token);
        }
    }
}
