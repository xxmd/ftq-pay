package com.example.pay.entity.enums;

public enum ZPayType {
    ALIPAY("alipay");
    private String value;

    ZPayType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
