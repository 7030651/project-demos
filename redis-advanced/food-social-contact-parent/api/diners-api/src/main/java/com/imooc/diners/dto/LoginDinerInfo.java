package com.imooc.diners.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginDinerInfo implements Serializable {
    private Integer id;
    private String nickname;
    private String token;
    private String avatarUrl;
}
