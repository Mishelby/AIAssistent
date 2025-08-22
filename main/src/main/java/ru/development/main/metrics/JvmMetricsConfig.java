package ru.development.main.metrics;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class JvmMetricsConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public JvmMetrics jvmMetrics() {
        JvmMetrics.builder().register();
        try {
            HTTPServer httpServer = HTTPServer.builder().port(9400).buildAndStart();
            log.info("HTTP server started on port: {}", httpServer.getPort());
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return jvmMetrics();
    }

    @Bean
    public Counter counter() {
        return Counter.builder()
                .name("http_request_gigachat")
                .help("Total number of HTTP request")
                .labelNames("method", "status")
                .register();
    }

    @Bean
    public Gauge gauge(){
        Gauge gauge = Gauge.builder()
                .name("memory_usage_bytes")
                .help("Current memory usage in bytes")
                .register(PrometheusRegistry.defaultRegistry);

        return gauge;
    }
}
