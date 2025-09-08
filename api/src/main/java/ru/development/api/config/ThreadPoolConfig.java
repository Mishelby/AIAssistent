package ru.development.api.config;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;

@Getter
@Setter
public final class ThreadPoolConfig {
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final long keepAliveTime;
    private final TimeUnit unit;
    private final BlockingQueue<Runnable> workQueue;
    private final ThreadFactory threadFactory;
    private final RejectedExecutionHandler rejectedExecutionHandler;
    private final String prefix;

    private ThreadPoolConfig(Builder builder) {
        this.corePoolSize = builder.corePoolSize;
        this.maximumPoolSize = builder.maximumPoolSize;
        this.keepAliveTime = builder.keepAliveTime;
        this.unit = builder.unit;
        this.workQueue = builder.workQueue;
        this.prefix = builder.prefix;
        this.threadFactory = nonNull(builder.threadFactory)
                ? builder.threadFactory
                : defaultThreadFactory(this.prefix);
        this.rejectedExecutionHandler = builder.rejectedExecutionHandler;
    }

    public ThreadPoolExecutor toExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                rejectedExecutionHandler
        );
    }

    private static ThreadFactory defaultThreadFactory(String prefix) {
        AtomicInteger counter = new AtomicInteger(1);
        return r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setName(prefix + counter.getAndIncrement());
            return t;
        };
    }

    public static class Builder {
        private int corePoolSize = Runtime.getRuntime().availableProcessors();
        private int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        private long keepAliveTime = 60;
        private TimeUnit unit = TimeUnit.SECONDS;
        private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100);
        private ThreadFactory threadFactory = Executors.defaultThreadFactory();
        private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
        private String prefix;

        public Builder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public Builder maximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public Builder keepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public Builder unit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }

        public Builder workQueue(BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
            return this;
        }

        public Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public Builder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this;
        }

        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public ThreadPoolConfig build() {
            return new ThreadPoolConfig(this);
        }
    }

}
