package ru.development.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@Configuration
public class ThreadPoolExecutoryConfig {
    private static final Integer CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final Integer MAXIMUM_CORE_PULL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final Integer KEEP_ALIVE_TIME = 30;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(

    ) {
        return new ThreadPoolConfig.Builder()
                .corePoolSize(CORE_POOL_SIZE)
                .maximumPoolSize(MAXIMUM_CORE_PULL_SIZE)
                .keepAliveTime(KEEP_ALIVE_TIME)
                .prefix("ThreadPoolExecutor")
                .build()
                .toExecutor();
    }
}
