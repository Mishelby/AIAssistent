package ru.development.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
@Slf4j
@Configuration
public class ExecutorThreadPoolConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        AtomicInteger counter = new AtomicInteger(1);
        int cores = Runtime.getRuntime().availableProcessors();
        log.info("[INFO] Количество свободных ядер при запуске приложения: {}", cores);

        return new ThreadPoolExecutor(
                cores,
                cores * 2,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                r -> {
                    Thread th = new Thread(r);
                    th.setName("custom-thread-" + counter.getAndIncrement());
                    th.setDaemon(true);
                    log.info("[INFO] custom-thread-{} started", th.getName());
                    log.info("[INFO] custom-thread-{} thread group name", th.getThreadGroup().getName());
                    return th;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
