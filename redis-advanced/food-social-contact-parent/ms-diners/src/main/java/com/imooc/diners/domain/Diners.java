package com.imooc.diners.domain;

import com.imooc.commons.model.base.BaseModel;
import lombok.*;

import javax.persistence.Column;

/**
 * @author E.T
 * @date 2022/12/5
 */
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diners extends BaseModel {

    private Integer id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String password;
    @Column(name = "avatar_url")
    private String avatarUrl;
    private String roles;
}
