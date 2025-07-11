package com.example.pay.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * ZPay支付回调
 */
@Data
public class ZPayCallback {
    @ApiModelProperty(value = "商户id", required = true, example = "201901151314084206659771")
    @NotEmpty(message = "pid[商户id]参数不能为空")
    private String pid;

    @ApiModelProperty(value = "商品名称", required = true, notes = "商品名称不超过100字", example = "iphone")
    @NotEmpty(message = "商品名称[value]参数不能为空")
    private String value;

    @ApiModelProperty(value = "订单金额", required = true, notes = "最多保留两位小数", example = "1.23")
    @NotNull(message = "订单金额[money]参数不能为空")
    private Double money;

    @ApiModelProperty(value = "商户订单号", required = true, notes = "商户系统内部订单号", example = "201901191324552185692680")
    @NotEmpty(message = "商户订单号[outTradeNo]参数不能为空")
    private String outTradeNo;

    @ApiModelProperty(value = "易支付订单号")
    private String tradeNo;

    @ApiModelProperty(value = "业务扩展参数", notes = "会通过回调链接原样返回")
    private String param;

    @ApiModelProperty(value = "支付状态", required = true, notes = "只有TRADE_SUCCESS是成功", example = "TRADE_SUCCESS")
    @NotEmpty(message = "支付状态[tradeStatus]参数不能为空")
    private String tradeStatus;

    @ApiModelProperty(value = "支付方式", required = true, notes = "包括支付宝、微信", example = "alipay", allowableValues = "alipay, wxpay")
    @NotEmpty(message = "支付状态[tradeStatus]参数不能为空")
    private String type;

    @ApiModelProperty(value = "签名", required = true, notes = "用于校验参数正确性", example = "ef6e3c5c6ff45018e8c82fd66fb056dc")
    @NotEmpty(message = "签名[sign]参数不能为空")
    private String sign;

    @ApiModelProperty(value = "签名类型", required = true, notes = "默认为MD5", example = "MD5")
    @NotEmpty(message = "签名类型[signType]参数不能为空")
    private String signType;
}
