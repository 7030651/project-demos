package com.imooc.feeds.entity;

/**
 * @author E.T
 * @date 2023/4/19
 */
import com.imooc.commons.model.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ApiModel(description = "Feed信息类")
public class Feeds extends BaseModel {

    @ApiModelProperty("内容")
    @Length(min = 1, max = 255, message = "内容长度必须在1-255之间")
    private String content;
    @ApiModelProperty("食客")
    private Integer fkDinerId;
    @ApiModelProperty("点赞")
    private int praiseAmount;
    @ApiModelProperty("评论")
    private int commentAmount;
    @ApiModelProperty("关联的餐厅")
    private Integer fkRestaurantId;

}