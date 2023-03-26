package com.imooc.diners.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginDinerInfo implements Serializable {
    private String nickname;
    private String token;
    private String avatarUrl;
}
