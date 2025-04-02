package com.example.orderservice.event.entity;

public enum EventType {
    EVENT_CREATED("추가"),
    EVENT_UPDATED("수정"),
    EVENT_DELETED("삭제");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
