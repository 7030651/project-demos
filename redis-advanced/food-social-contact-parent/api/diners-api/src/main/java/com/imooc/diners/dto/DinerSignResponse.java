package com.imooc.diners.dto;

import com.imooc.commons.model.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author E.T
 * @date 2023/4/20
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DinerSignResponse extends BaseResponse {
    private DinerSignRespVO data;
}
