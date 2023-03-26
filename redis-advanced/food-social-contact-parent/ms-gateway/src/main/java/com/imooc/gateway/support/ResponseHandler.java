package com.imooc.gateway.support;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Component
public class ResponseHandler {
    @Resource
    private ObjectMapper objectMapper;

    public Mono<Void> writeError(ServerWebExchange exchange, String errorMsg) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResultInfo resultInfo = ResultInfoUtil.buildError(ApiConstant.NO_LOGIN_CODE, errorMsg, request.getURI().getPath());
        String resultInfoJson = null;
        DataBuffer buffer = null;
        try {
            // TODO 非阻塞解决方案？
            resultInfoJson = objectMapper.writeValueAsString(resultInfo);
            buffer =  response.bufferFactory().wrap(resultInfoJson.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        assert buffer != null;
        return response.writeWith(Mono.just(buffer));
    }
}
