package com.github.gkislin.common.schedule;

import com.github.gkislin.common.LoggerWrapper;
import com.github.gkislin.common.util.AsyncExecutor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * User: gkislin
 * Date: 06.09.12
 */
public class SchedulerListener implements ServletContextListener, Scheduler {
    protected final LoggerWrapper logger = LoggerWrapper.get(getClass());
    private static Scheduler SCHEDULER;

    @SuppressWarnings("unchecked")
    public static <T extends Scheduler> T getScheduler() {
        return (T) SCHEDULER;
    }

    @Override
    public final void contextInitialized(ServletContextEvent sce) {
        SCHEDULER = this;

        try {
            activate();
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public final void contextDestroyed(ServletContextEvent sce) {
        try {
            deactivate();
            AsyncExecutor.shutdown();
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }
}
