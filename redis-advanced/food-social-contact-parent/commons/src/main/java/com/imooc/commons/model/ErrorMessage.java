package com.imooc.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author E.T
 * @date 2023/4/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorMessage {
    /** 错误字段 */
    private String field;
    /** 错误消息 */
    private String message;
}
