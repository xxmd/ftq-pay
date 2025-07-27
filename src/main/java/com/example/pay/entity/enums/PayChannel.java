package com.example.pay.entity.enums;

public enum PayChannel {
    ZPAY("ZPay");
    private String value;

    PayChannel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
