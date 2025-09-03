package ru.development.core.httpCore.httpClient;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import ru.development.core.httpCore.retryPolice.RetryPolicyProvider;


@Slf4j
@Value
@Accessors(fluent = true)
public class HttpClientConnection implements HttpClientMethods {
    IHttpCore iHttpCore;
    RetryPolicyProvider retryPolicyProvider;

    public HttpClientConnection(Builder builder) {
        this.iHttpCore = builder.iHttpCore;
        this.retryPolicyProvider = builder.retryTemplate;
    }

    public HttpClientConnection(IHttpCore iHttpCore, RetryPolicyProvider retryPolicyProvider) {
        this.iHttpCore = iHttpCore;
        this.retryPolicyProvider = retryPolicyProvider;
    }

    public HttpClientConnection(IHttpCore iHttpCore) {
        this.iHttpCore = iHttpCore;
        this.retryPolicyProvider = null;
    }


    @Getter
    @Setter
    public static class Builder {
        private IHttpCore iHttpCore;
        private RetryPolicyProvider retryTemplate;

        public Builder iHttpCore(IHttpCore iHttpCore) {
            this.iHttpCore = iHttpCore;
            return this;
        }

        public Builder retryTemplate(RetryPolicyProvider retryTemplate) {
            this.retryTemplate = retryTemplate;
            return this;
        }

        public HttpClientConnection build() {
            return new HttpClientConnection(this);
        }
    }
}
