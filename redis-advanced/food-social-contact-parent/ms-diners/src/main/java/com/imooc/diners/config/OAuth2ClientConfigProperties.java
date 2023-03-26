package com.imooc.diners.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth2.client")
@Setter
@Getter
@ToString
public class OAuth2ClientConfigProperties {
    private String clientId;
    private String secret;
    private String grantType;
    private String scope;
}
