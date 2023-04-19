package com.imooc.commons.config;

import com.imooc.commons.auth.FeignRequestHeaderInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author E.T
 * @date 2023/4/19
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return new FeignRequestHeaderInterceptor();
    }
}
