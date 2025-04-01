package com.example.userservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource routingDataSource(
            @Qualifier("shard1DataSource") DataSource shard1,
            @Qualifier("shard2DataSource") DataSource shard2) {

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(0, shard1);
        targetDataSources.put(1, shard2);

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(shard1);
        return routingDataSource;
    }

    @Bean("shard1DataSource")
    public DataSource shard1() {
        return DataSourceBuilder.create()
                .url("jdbc:mariadb://localhost:3307/shard1")
                .username("root")
                .password("test1357")
                .build();
    }

    @Bean("shard2DataSource")
    public DataSource shard2() {
        return DataSourceBuilder.create()
                .url("jdbc:mariadb://localhost:3307/shard2")
                .username("root")
                .password("test1357")
                .build();
    }
}