package ru.development.core.httpCore.httpClient;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
@Value
public class HttpClientConnection implements HttpClientMethods {
    IHttpCore iHttpCore;
    RetryTemplate retryTemplate;

    public HttpClientConnection(Builder builder) {
        this.iHttpCore = builder.iHttpCore;
        this.retryTemplate = builder.retryTemplate;
    }

    @Getter
    @Setter
    public static class Builder {
        private IHttpCore iHttpCore;
        private RetryTemplate retryTemplate;

        public Builder iHttpCore(IHttpCore iHttpCore) {
            this.iHttpCore = iHttpCore;
            return this;
        }

        public Builder restTemplate(RetryTemplate retryTemplate) {
            this.retryTemplate = retryTemplate;
            return this;
        }

        public HttpClientConnection build() {
            return new HttpClientConnection(this);
        }
    }
}
