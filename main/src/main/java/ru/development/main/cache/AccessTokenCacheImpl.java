package ru.development.main.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import ru.development.main.model.AccessToken;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

/**
 * TODO Надо добавить хранение кеша локально, т.к сейчас он удаляется после каждого запуска
 * Scheduled надо переделать под потоки? Сейчас он независимо чистит кеш каждые x минут
 */

@Slf4j
@Getter
@Setter
public class AccessTokenCacheImpl implements AccessTokenCache {
    private final Map<String, AccessToken> accessTokens;
    private final Duration cashDuration;

    private AccessTokenCacheImpl(Builder builder) {
        this.accessTokens = nonNull(builder.map)
                ? builder.map
                : new ConcurrentHashMap<>();

        this.cashDuration = nonNull(builder.duration)
                ? builder.duration
                : Duration.ofHours(1L);
    }

    @Scheduled(fixedRateString = "${cache.fixedRateValue}")
    public void clearCache() {
        accessTokens.clear();
    }

    @Override
    public AccessToken get(String key) {
        return accessTokens.get(key);
    }

    @Override
    public void put(String key, AccessToken accessToken) {
        accessTokens.put(key, accessToken);
    }

    @Override
    public void remove(String key) {
        accessTokens.remove(key);
    }

    @Override
    public boolean containsKey(String key) {
        return accessTokens.containsKey(key);
    }

    @Override
    public String getFullInfo() {
        return "Cache info: tokens " + accessTokens + " duration " + cashDuration;
    }

    @Getter
    @Setter
    public static class Builder {
        private Map<String, AccessToken> map;

        private Duration duration;

        public Builder durationTime(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder withMap(Map<String, AccessToken> map) {
            this.map = map;
            return this;
        }

        public AccessTokenCacheImpl build() {
            return new AccessTokenCacheImpl(this);
        }
    }
}
