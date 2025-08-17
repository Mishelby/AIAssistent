package ru.development.core.httpCore.httpClient;

import ch.qos.logback.core.net.ssl.SSL;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

public class HttpClientBuilderImpl implements HttpClientBuilder<HttpClient>{
    private java.net.http.HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
    private Duration connectTimeout;
    private Duration readTimeout;
    private SSL ssl;
    private Map<String, String> customHeaders;
    private Function<HttpClient, HttpClient> decorator;

    public java.net.http.HttpClient.Builder httpClientBuilder() {
        return httpClientBuilder;
    }

    public HttpClientBuilderImpl httpClientBuilder(java.net.http.HttpClient.Builder httpClientBuilder) {
        this.httpClientBuilder = httpClientBuilder;
        return this;
    }

    @Override
    public Duration connectTimeout() {
        return connectTimeout;
    }

    @Override
    public HttpClientBuilder<HttpClient> connectTimeout(Duration timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    @Override
    public Duration readTimeout() {
        return readTimeout;
    }

    @Override
    public HttpClientBuilder<HttpClient> readTimeout(Duration timeout) {
        this.readTimeout = timeout;
        return this;
    }

    @Override
    public HttpClientImpl build() {
        return new HttpClientImpl(this);
    }

    @Override
    public SSL ssl() {
        return ssl;
    }

    @Override
    public HttpClientBuilder<HttpClient> ssl(SSL ssl) {
        this.ssl = ssl;
        return this;
    }

    @Override
    public Map<String, String> customHeaders() {
        return customHeaders;
    }

    @Override
    public Function<HttpClient, HttpClient> decorator() {
        return decorator;
    }

    @Override
    public HttpClientBuilder<HttpClient> decorator(Function<HttpClient, HttpClient> decorator) {
        this.decorator = decorator;
        return this;
    }

    @Override
    public HttpClientBuilder<HttpClient> customHeaders(Map customHeaders) {
        this.customHeaders = customHeaders;
        return this;
    }
}
