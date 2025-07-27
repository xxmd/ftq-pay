package com.example.pay.entity;

import com.example.pay.entity.enums.ZPayType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * ZPay订单
 */
@Data
public class ZPayOrder {
    @ApiModelProperty(value = "商品名称", required = true, example = "默认商品名称")
    @NotEmpty(message = "商品名称不能为空")
    private String name;

    @ApiModelProperty(value = "商品价格", required = true, notes = "最多保留两位小数", example = "0.01")
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", inclusive = true, message = "商品价格必须大于 0")
    @Digits(integer = 10, fraction = 2, message = "最多保留两位小数")
    private BigDecimal money;

    @ApiModelProperty(value = "支付方式", required = true, example = "ALIPAY")
    @NotNull(message = "支付不能为空")
    private ZPayType type;

    @ApiModelProperty(value = "跳转链接", required = true, example = "https://baidu.com")
    private String returnUrl;
}
