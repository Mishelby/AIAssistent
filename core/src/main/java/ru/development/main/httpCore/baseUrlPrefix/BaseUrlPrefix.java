package ru.development.main.httpCore.baseUrlPrefix;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "system")
@Getter
@Setter
public class BaseUrlPrefix {
    public String baseUrl;
}
