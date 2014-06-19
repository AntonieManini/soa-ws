package com.github.gkislin.common.schedule;

import com.github.gkislin.common.LoggerWrapper;
import com.github.gkislin.common.util.AsyncExecutor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * User: gkislin
 * Date: 06.09.12
 */
public class SchedulerListener implements ServletContextListener {
    protected final LoggerWrapper logger = LoggerWrapper.get(getClass());

    @Override
    public final void contextInitialized(ServletContextEvent sce) {
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

    protected void activate() {
    }

    protected void deactivate() {
    }
}
