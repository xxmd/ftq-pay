package com.example.pay.entity;

import com.example.pay.entity.enums.ZPayType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ZPayOrder {
    @ApiModelProperty(value = "商品名称", required = true, example = "iphone11")
    @NotEmpty(message = "商品名称不能为空")
    private String name;
    @ApiModelProperty(value = "商品价格", required = true, notes = "最多保留两位小数", example = "1.23")
    @NotNull(message = "商品价格不能为空")
    private Double money;
    @ApiModelProperty(value = "商品价格", required = true, notes = "最多保留两位小数", example = "ALIPAY")
    @NotNull(message = "支付不能为空")
    private ZPayType type;
    @ApiModelProperty(value = "通知链接", required = true, example = "https://xx.com/notify")
    private String notifyUrl;
    @ApiModelProperty(value = "跳转链接", required = true, example = "https://baidu.com")
    private String returnUrl;
}
