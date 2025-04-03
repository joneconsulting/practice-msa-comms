package com.example.shippingservice.config;

public enum ShippingStatus {
    PENDING("확인중"),
    CANCELLED("취소"),
    COMPLETED("완료"),
    FAILED("실패"),
    SHIPPED("배송시작")
    ;

    private final String displayName;

    ShippingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
