package com.imooc.gateway.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "secure.ignore")
public class IgnoreUrlsProperties {

    private List<String> urls;
}
