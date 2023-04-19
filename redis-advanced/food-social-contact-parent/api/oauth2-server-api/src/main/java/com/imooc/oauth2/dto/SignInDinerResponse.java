package com.imooc.oauth2.dto;

import com.imooc.commons.model.BaseResponse;
import com.imooc.commons.model.vo.SignInDinerInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author E.T
 * @date 2023/4/19
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInDinerResponse extends BaseResponse {
    SignInDinerInfo data;
}
