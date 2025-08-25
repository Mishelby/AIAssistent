package ru.development.main.cache;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "cache")
@Getter
@Setter
public class CachePrefix {
    private @NonNull Long fixedRateValue;
}
