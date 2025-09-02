package ru.development.core.httpCore.retryPolice;

import org.springframework.retry.support.RetryTemplate;

public interface RetryPolicyProvider {
    RetryTemplate getRetryTemplate();
}
