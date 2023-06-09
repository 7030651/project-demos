package com.imooc.oauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "client.oauth2")
@Setter
@Getter
public class ClientOAuth2DataConfiguration {
    /** 客户端标识 ID */
    private String clientId;

    /** 客户端安全码 */
    private String secret;

    /** 授权类型 */
    private String[] grantTypes;

    /** token有效期 */
    private int tokenValidityTime;

    /** refresh-token有效期 */
    private int refreshTokenValidityTime;

    /** 客户端访问范围 */
    private String[] scopes;
}
