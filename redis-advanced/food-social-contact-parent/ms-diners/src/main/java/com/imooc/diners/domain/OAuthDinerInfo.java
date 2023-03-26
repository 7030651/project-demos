package com.imooc.diners.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OAuthDinerInfo implements Serializable {

    private String nickname;
    private String avatarUrl;
    private String accessToken;
    private String expireIn;
    private List<String> scopes;
    private String refreshToken;
}
