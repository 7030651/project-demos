package com.imooc.diners.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author E.T
 * @date 2023/4/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DinerSignRespVO {
    String date;
    long days;
}
