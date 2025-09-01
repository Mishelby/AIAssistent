package ru.development.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;


@Slf4j
@Configuration
@EnableRetry
public class RetryConfig {
    @Bean
    public RetryTemplate httpRetryTemplate() {
        RetryTemplate httpRetryTemplate = new RetryTemplate();
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000L);
        httpRetryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(3);
        httpRetryTemplate.setRetryPolicy(simpleRetryPolicy);

        return httpRetryTemplate;
    }
}
