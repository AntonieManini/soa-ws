package com.github.gkislin.common;

import com.github.gkislin.common.web.FaultInfo;
import com.github.gkislin.common.web.WebStateException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.sql.BatchUpdateException;

/**
 * User: gkislin
 * Date: 22.01.14
 */
public class LoggerWrapper {

    private Logger logger;

    public LoggerWrapper(Logger logger) {
        this.logger = logger;
    }

    public static LoggerWrapper get(Class aClass) {
        return new LoggerWrapper(Logger.getLogger(aClass));
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    public void error(Exception e) {
        logger.error(e);
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    public boolean isDebug() {
        return logger.isDebugEnabled();
    }

    public IllegalStateException getIllegalStateException(String msg) {
        return getIllegalStateException(msg, null);
    }

    public IllegalStateException getIllegalStateException(String msg, @Nullable Throwable e) {
        logger.error(msg, e);
        return new IllegalStateException(msg, e);
    }

    public IllegalStateException getIllegalStateException(Throwable e) {
        logger.error(e);
        return new IllegalStateException(e);
    }

    public IllegalArgumentException getIllegalArgumentException(String msg) {
        return getIllegalArgumentException(msg, null);
    }

    public IllegalArgumentException getIllegalArgumentException(String msg, @Nullable Throwable e) {
        logger.error(msg, e);
        return new IllegalArgumentException(msg, e);
    }

    public UnsupportedOperationException getUnsupportedOperationException(String msg) {
        logger.error(msg);
        return new UnsupportedOperationException(msg);
    }

    public StateException getStateException(WebStateException e) {
        FaultInfo faultInfo = e.getFaultInfo();
        return getStateException(e.getMessage(), faultInfo.getType(), faultInfo.getSource());
    }

    public StateException getStateException(String msg, ExceptionType type, ExceptionSource source) {
        logger.error(msg);
        return new StateException(msg, type, source);
    }

    public StateException getStateException(String msg, ExceptionType type) {
        logger.error(msg);
        return new StateException(msg, type);
    }

    public StateException getStateException(ExceptionType type, Throwable e) {
        return getStateException(e.toString(), type, e);
    }

    public StateException getStateException(String msg, ExceptionType type, @Nullable Throwable e) {
        Throwable cause;
        if (e != null) {
            while (true) {
                if (e instanceof StateException) {
                    return (StateException) e;
                } else if (e instanceof BatchUpdateException) {
                    cause = ((BatchUpdateException) e).getNextException();
                } else {
                    cause = e.getCause();
                }
                if (cause == null) break;
                e = cause;
            }
        }
        logger.error(msg, e);
        return new StateException(msg, type, e);
    }
}
