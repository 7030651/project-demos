package com.imooc.gateway.filter;

import com.imooc.gateway.config.IgnoreUrlsProperties;
import com.imooc.gateway.support.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    @Resource
    private IgnoreUrlsProperties ignoreUrlsProperties;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ResponseHandler responseHandler;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("AuthFilter " + exchange.getClass().getSimpleName());
        // 1. 是否在白名单中，是则放行。
        AntPathMatcher pathMatcher = new AntPathMatcher();
        log.info("getPath = {}, getURI = {}", exchange.getRequest().getPath().toString(),
                exchange.getRequest().getURI().getPath());

        String path = exchange.getRequest().getURI().getPath();
        boolean matched = ignoreUrlsProperties.getUrls().stream()
                .anyMatch(url -> {
//                    System.err.println("url = " + url + ", path = " + path + ", matched = " + pathMatcher.match(url, path));
                    return pathMatcher.match(url, path);
                });

        if (matched) {
            return chain.filter(exchange);
        }

        String first = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        List<String> list = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        // 获取 access_token
        String accessToken = parseToken(exchange.getRequest());
        // 判断 access_token 是否为空
        if (accessToken == null) {
            // TODO: response 与远程调用的 service 中的 response 冲突。
            return responseHandler.writeError(exchange, "请登录");
        }
        // 校验 token 是否有效
        String checkTokenUrl = "http://ms-oauth2-server/oauth/check_token?token=".concat(accessToken);
        try {
            // 发送远程请求，验证 token
            ResponseEntity<String> entity = restTemplate.getForEntity(checkTokenUrl, String.class);
            // token 无效的业务逻辑处理
            if (entity.getStatusCode() != HttpStatus.OK) {
                return responseHandler.writeError(exchange,
                        "Token was not recognised, token: ".concat(accessToken));
            }
            if (StringUtils.isBlank(entity.getBody())) {
                return responseHandler.writeError(exchange,
                        "This token is invalid: ".concat(accessToken));
            }
        } catch (Exception e) {
            return responseHandler.writeError(exchange,
                    "Token was not recognised, token: ".concat(accessToken));
        }
        // 放行
        return chain.filter(exchange);
    }

    private String parseToken(ServerHttpRequest request) {
        String token = request.getQueryParams().getFirst("access_token");
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(token)) {
            token = token.replace("Bearer ", "");
        }
        return token;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
