package com.example.userservice.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<Integer> shardKey = new ThreadLocal<>();

    public static void setShardKey(int userId) {
        shardKey.set(userId % 2); // 예: 2개 샤드
    }

    public static void clear() {
        shardKey.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return shardKey.get();
    }
}