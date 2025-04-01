package com.example.userservice.utils;

public class ShardingKeyUtil {

    /**
     * UUID 문자열을 받아 해시 기반 샤딩 키(int)로 변환
     */
    public static int uuidToShardKey(String uuid, int shardCount) {
        return Math.abs(uuid.hashCode()) % shardCount;
    }
}