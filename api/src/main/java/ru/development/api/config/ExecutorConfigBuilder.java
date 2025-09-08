package ru.development.api.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public interface ExecutorConfigBuilder  {
    int corePoolSize();

    ThreadPoolBuilder corePoolSize(int corePoolSize);

    int maximumPoolSize();

    ThreadPoolBuilder maximumPoolSize(int maximumPoolSize);

    long keepAliveTime();

    ThreadPoolBuilder keepAliveTime(long timeout);

    TimeUnit unit();

    ThreadPoolBuilder unit(TimeUnit unit);

    BlockingQueue<Runnable> workQueue();

    ThreadPoolBuilder workQueue(BlockingQueue<Runnable> queue);

    ThreadFactory threadFactory();

    ThreadPoolBuilder threadFactory(ThreadFactory threadFactory);

    RejectedExecutionHandler rejectedExecutionHandler();

    ThreadPoolBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler);

    ExecutorConfig build();
}
