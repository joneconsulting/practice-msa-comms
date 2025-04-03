package com.example.paymentservice.config;

public enum PaymentStatus {
    PENDING("확인중"),
    CANCELLED("취소"),
    COMPLETED("완료"),
    FAILED("실패")
    ;

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
