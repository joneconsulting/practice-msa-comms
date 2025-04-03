package com.example.orderservice.config;

public enum OrderStatus {
    PENDING("확인중"),
    CANCELLED("취소"),
    COMPLETED("완료")
    ;

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
