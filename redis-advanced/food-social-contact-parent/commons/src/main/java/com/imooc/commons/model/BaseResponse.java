package com.imooc.commons.model;

import com.imooc.commons.constant.ApiConstant;
import lombok.*;

/**
 * @author E.T
 * @date 2023/4/19
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BaseResponse {
    private String message;
    private int code = ApiConstant.SUCCESS_CODE;

    public static BaseResponse success() {
        return BaseResponse.builder().message("success").code(ApiConstant.SUCCESS_CODE)
                .build();
    }
}
