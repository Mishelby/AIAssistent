package ru.development.core.httpCore.retryPolice;

import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component("fixedDelayRetryPolicy")
public class FixedDelayRetryPolicy implements RetryPolicyProvider{
    @Override
    public RetryTemplate getRetryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(3)
                .fixedBackoff(2000)
                .build();
    }
}
