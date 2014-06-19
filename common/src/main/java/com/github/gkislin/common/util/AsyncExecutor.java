package com.github.gkislin.common.util;


import com.github.gkislin.common.LoggerWrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: gkislin
 * Date: 06.02.14
 */
public class AsyncExecutor {
    private static final LoggerWrapper LOGGER = LoggerWrapper.get(AsyncExecutor.class);
    private static final ScheduledThreadPoolExecutor EXECUTOR;

    static {
        EXECUTOR = new ScheduledThreadPoolExecutor(150); //max thread
        EXECUTOR.setKeepAliveTime(5, TimeUnit.SECONDS);
        EXECUTOR.allowCoreThreadTimeOut(true);  // allow terminate core idle threads
    }

    static final Map<Runnable, Future<?>> tasks = new HashMap<>();

    public static void setPoolSize(int size) {
        EXECUTOR.setCorePoolSize(size);
    }

    public static Future<?> submit(final Runnable task) {
        LOGGER.info("Submit asynchronous task");
        return EXECUTOR.submit(wrap(task));
    }

    // Simple execute without wrap
    public static void execute(final Runnable task) {
        LOGGER.info("Execute asynchronous task");
        EXECUTOR.execute(task);
    }

    public static synchronized Runnable schedule(final Runnable task, long delay, long interval, TimeUnit unit) {
        LOGGER.info("Schedule asynchronous task");
        tasks.put(task,
                EXECUTOR.scheduleWithFixedDelay(wrap(task), delay, interval, unit));
        return task;
    }

    public static synchronized void cancel(Runnable task, boolean mayInterruptIfRunning) {
        LOGGER.info("Cancel asynchronous task");
        Future<?> future = tasks.remove(task);
        if (future != null) {
            future.cancel(mayInterruptIfRunning);
        }
    }

    public static Future<?> submit(Runnable task, boolean async) {
        Future<?> future = null;
        if (async) {
            future = submit(task);
        } else {
            task.run();
        }
        return future;
    }

    public void cancelAll() throws Exception {
        LOGGER.info("Cancel all asynchronous task");
        synchronized (AsyncExecutor.class) {
            Iterator<Runnable> iter = tasks.keySet().iterator();
            while (iter.hasNext()) {
                Runnable next = iter.next();
                tasks.get(next).cancel(true);
                iter.remove();
            }
        }
    }

    private static Runnable wrap(final Runnable task) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    LOGGER.error("AsyncExecutor exception", e);
                }
            }
        };
    }

    public static void shutdown() {
        EXECUTOR.shutdown();
        try {
            if (!EXECUTOR.awaitTermination(3, TimeUnit.SECONDS)) {
                EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) { //nothing
        }
    }

    public static void waitFuture(Future<?> future) throws ExecutionException, InterruptedException {
        LOGGER.info("Wait asynchronous task");
        future.get();
    }
}
