package com.imooc.commons.model.base;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体对象公共属性
 */
@Data
public class BaseModel implements Serializable {

    private Integer id;
    private Date createDate;
    private Date updateDate;
    private int isValid;

}