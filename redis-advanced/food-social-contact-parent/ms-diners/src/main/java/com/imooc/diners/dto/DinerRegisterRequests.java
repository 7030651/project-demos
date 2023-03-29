package com.imooc.diners.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author E.T
 * @date 2022/12/5
 * TODO: move to diners-api.
 */
@Data
@ApiModel(description = "注册用户信息")
public class DinerRegisterRequests implements Serializable {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty("用户名")
    private String username;
    private String nickname;
    @NotBlank
    @ApiModelProperty("密码")
    private String password;
    @NotBlank
    @ApiModelProperty("手机号")
    private String phone;

    @NotBlank
    @ApiModelProperty("验证码")
    @JsonProperty(value = "verify_code")
    private String verifyCode;

    @NotBlank
    @JsonProperty(value = "avatar_url")
    private String avatarUrl;

}
