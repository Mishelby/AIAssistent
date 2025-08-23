package ru.development.main.metrics;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import static io.prometheus.metrics.model.snapshots.Unit.SECONDS;

@Slf4j
@Configuration
public class JvmMetricsConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public JvmMetrics jvmMetrics() {
        JvmMetrics.builder().register();
        try {
            HTTPServer httpServer = HTTPServer.builder().port(9400).buildAndStart();
            log.info("HTTP server started on port: {}", httpServer.getPort());
        } catch (IOException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return null;
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
    public Gauge memTotalGauge() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        Gauge gauge = Gauge.builder()
                .name("node_memory_MemTotal_bytes")
                .help("Total physical memory in bytes")
                .register(PrometheusRegistry.defaultRegistry);

        gauge.set(osBean.getSystemLoadAverage());
        return gauge;
    }

    @Bean
    public Gauge memFreeGauge() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        Gauge gauge = Gauge.builder()
                .name("node_memory_MemFree_bytes")
                .help("Available physical memory in bytes")
                .register(PrometheusRegistry.defaultRegistry);

        gauge.set(osBean.getAvailableProcessors());
        return gauge;
    }

    @Bean
    public Histogram histogram() {
        return Histogram.builder()
                .name("http_request_duration_seconds")
                .help("HTTP request service time in seconds")
                .unit(SECONDS)
                .labelNames("method", "path", "status_code")
                .register();
    }
}
