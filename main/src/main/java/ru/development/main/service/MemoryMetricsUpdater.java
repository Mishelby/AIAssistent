package ru.development.main.service;

import io.prometheus.metrics.core.metrics.Gauge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

@Component
public class MemoryMetricsUpdater {
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

    private final Gauge memFreeGauge;
    private final Gauge memTotalGauge;

    public MemoryMetricsUpdater(Gauge memFreeGauge, Gauge memTotalGauge) {
        this.memFreeGauge = memFreeGauge;
        this.memTotalGauge = memTotalGauge;
    }

    @Scheduled(fixedRate = 10000)
    public void updateMemoryMetrics() {
        memFreeGauge.set(osBean.getSystemLoadAverage());
        memTotalGauge.set(osBean.getAvailableProcessors());
    }
}
