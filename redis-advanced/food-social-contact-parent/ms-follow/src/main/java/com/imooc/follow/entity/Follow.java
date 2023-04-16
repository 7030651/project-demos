package com.imooc.follow.entity;

import com.imooc.commons.model.base.BaseModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author E.T
 * @date 2023/4/16
 */
@Setter
@Getter
@ToString(callSuper = true)
@Accessors(chain = true)
public class Follow extends BaseModel {
    private Integer dinerId;
    private Integer followDinerId;
}
