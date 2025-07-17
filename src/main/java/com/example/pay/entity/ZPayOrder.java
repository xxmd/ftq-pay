package com.example.pay.entity;

import com.example.pay.entity.enums.ZPayType;
import lombok.Data;

@Data
public class ZPayOrder {
    private String name;
    private Double money;
    private ZPayType type;
}
