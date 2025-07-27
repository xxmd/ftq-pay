package com.example.pay.entity;

import com.example.pay.entity.enums.OrderStatus;
import com.example.pay.entity.enums.PayChannel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class OrderInfo extends BaseEntity {
    @Enumerated(EnumType.STRING)
    // 支付渠道
    private PayChannel payChannel;

    // 订单流水号
    private String serialNumber;

    // 产品名称
    private String productName;

    // 产品价格
    private BigDecimal productPrice;

    // 订单状态
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
