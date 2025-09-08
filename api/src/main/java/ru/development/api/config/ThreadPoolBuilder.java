package ru.development.api.config;


import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Component
@Getter
public class ThreadPoolBuilder implements ExecutorConfigBuilder  {
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private TimeUnit unit;
    private BlockingQueue<Runnable> workQueue;
    private ThreadFactory threadFactory;
    private RejectedExecutionHandler rejectedExecutionHandler;

    @Override
    public int corePoolSize() {
        return corePoolSize;
    }

    @Override
    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    @Override
    public int maximumPoolSize() {
        return maximumPoolSize;
    }

    @Override
    public ThreadPoolBuilder maximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    @Override
    public long keepAliveTime() {
        return keepAliveTime;
    }

    @Override
    public ThreadPoolBuilder keepAliveTime(long timeout) {
        this.keepAliveTime = timeout;
        return this;
    }

    @Override
    public TimeUnit unit() {
        return unit;
    }

    @Override
    public ThreadPoolBuilder unit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }

    @Override
    public BlockingQueue<Runnable> workQueue() {
        return workQueue;
    }

    @Override
    public ThreadPoolBuilder workQueue(BlockingQueue<Runnable> queue) {
        this.workQueue = queue;
        return this;
    }

    @Override
    public ThreadFactory threadFactory() {
        return threadFactory;
    }

    @Override
    public ThreadPoolBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    @Override
    public RejectedExecutionHandler rejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    @Override
    public ThreadPoolBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    @Override
    public ExecutorConfig build() {
        return new ThreadPoolExecutorImlp(this);
    }

}
