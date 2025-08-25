package ru.development.main.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.development.main.cache.AccessTokenCache;
import ru.development.main.cache.AccessTokenCacheImpl;
import ru.development.main.cache.CachePrefix;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
public class CashConfig {
    private final CachePrefix cachePrefix;

    public CashConfig(CachePrefix cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    @Bean
    public AccessTokenCache accessTokenCache() {
        return new AccessTokenCacheImpl.Builder()
                .durationTime(Duration.ofMillis(cachePrefix.getFixedRateValue()))
                .withMap(new ConcurrentHashMap<>())
                .build();
    }

}
