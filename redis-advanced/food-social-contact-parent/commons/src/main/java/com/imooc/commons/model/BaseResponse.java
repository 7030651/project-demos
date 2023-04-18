package com.imooc.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private int code = 200;
}
