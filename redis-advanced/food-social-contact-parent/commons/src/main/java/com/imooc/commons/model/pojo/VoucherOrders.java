package com.imooc.commons.model.pojo;

import cn.hutool.core.util.IdUtil;
import com.imooc.commons.model.base.BaseModel;
import com.imooc.commons.model.vo.SignInDinerInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@ApiModel(description = "代金券订单信息")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class VoucherOrders extends BaseModel {

    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("代金券")
    private Integer fkVoucherId;
    @ApiModelProperty("下单用户")
    private Integer fkDinerId;
    @ApiModelProperty("生成qrcode")
    private String qrcode;
    @ApiModelProperty("支付方式 0=微信支付 1=支付宝")
    private int payment;
    @ApiModelProperty("订单状态 -1=已取消 0=未支付 1=已支付 2=已消费 3=已过期")
    private int status;
    @ApiModelProperty("订单类型 0=正常订单 1=抢购订单")
    private int orderType;
    @ApiModelProperty("抢购订单的外键")
    private int fkSeckillId;

    public VoucherOrders(SeckillVouchers seckillVouchers, SignInDinerInfo dinerInfo) {
        this.fkDinerId = dinerInfo.getId();
        this.fkVoucherId = seckillVouchers.getFkVoucherId();
        // 使用 redis 时不需要维护该外键。
        if (seckillVouchers.getId() != null) {
            this.fkSeckillId = seckillVouchers.getId();
        }
        this.orderNo = IdUtil.getSnowflake(1, 1).nextIdStr();
        this.orderType = 1;
        this.status = 0;
    }

}