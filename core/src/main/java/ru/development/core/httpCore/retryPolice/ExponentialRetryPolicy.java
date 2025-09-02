package ru.development.core.httpCore.retryPolice;

import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component("exponentialRetryPolicy")
public class ExponentialRetryPolicy implements RetryPolicyProvider {
    @Override
    public RetryTemplate getRetryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(5)
                .exponentialBackoff(1000, 2, 10000)
                .build();
    }
}
