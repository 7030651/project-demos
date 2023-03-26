package com.imooc.oauth.config;

import com.imooc.commons.model.domain.SignInIdentity;
import com.imooc.oauth.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 授权服务配置
 */

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Resource
    private UserService userService;
    @Resource
    private RedisTokenStore redisTokenStore;

    @Resource
    private ClientOAuth2DataConfiguration clientOAuth2DataConfiguration;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * 配置令牌端点安全约束
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 允许访问 token 的公钥，默认 /oauth/token_key 是受保护的
        security.tokenKeyAccess("permitAll()")
                // 允许检查 token 的状态，默认 /oauth/check_token 是受保护的
                .checkTokenAccess("permitAll()");
    }

    /**
     * 客户端配置 - 授权模型
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient(clientOAuth2DataConfiguration.getClientId()) // 客户端标识 ID
                .secret(passwordEncoder.encode(clientOAuth2DataConfiguration.getSecret())) // 客户端安全码
                .authorizedGrantTypes(clientOAuth2DataConfiguration.getGrantTypes()) // 授权类型
                .accessTokenValiditySeconds(clientOAuth2DataConfiguration.getTokenValidityTime()) // token 有效期
                .refreshTokenValiditySeconds(clientOAuth2DataConfiguration.getRefreshTokenValidityTime()) // 刷新 token 的有效期
                .scopes(clientOAuth2DataConfiguration.getScopes()); // 客户端访问范围
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userService)
                // token 写入 redis.
                .tokenStore(redisTokenStore)
                // 令牌增强，扩展返回值。
                .tokenEnhancer((accessToken, auth) -> {
                    DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
                    SignInIdentity user = (SignInIdentity) auth.getPrincipal();
                    token.setAdditionalInformation(
                            Map.of("nickname", user.getNickname(),
                                    "avatarUrl", user.getAvatarUrl()));
                    return token;
                });
    }
}
