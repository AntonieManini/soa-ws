package com.github.gkislin.common.schedule;

import com.github.gkislin.common.LoggerWrapper;
import com.github.gkislin.common.util.AsyncExecutor;

import java.util.concurrent.TimeUnit;

/**
 * User: gkislin
 * Date: 10.02.14
 */
public class AsyncTask {
    protected final LoggerWrapper logger = LoggerWrapper.get(getClass());
    protected Runnable runTask = null;

    public void activate(Runnable runnable, long interval, boolean isRunFirst) {
        deactivate();
        if (interval == 0) {
            logger.info("interval=0, scanning disabled");
        } else {
            logger.info("Start scanning with interval " + interval+ " sec");
            runTask = AsyncExecutor.schedule(runnable, isRunFirst ? 0 : interval, interval, TimeUnit.SECONDS);
        }
    }

    public void deactivate() {
        if (runTask != null) {
            logger.info("Stop scanning");
            AsyncExecutor.cancel(runTask, true);
            runTask = null;
        }
    }
}
