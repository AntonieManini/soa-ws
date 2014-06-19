package com.github.gkislin.common.web;

import com.github.gkislin.common.LoggerWrapper;
import com.github.gkislin.common.StateException;

/**
 * User: gkislin
 * Date: 19.06.2014
 */
public class WebStateExceptionUtil {
    private static final LoggerWrapper LOGGER = LoggerWrapper.get(WebStateExceptionUtil.class);

    public static WebStateException getWebStateException(Exception e) {
        if (e instanceof StateException) {
            StateException se = (StateException) e;
            return new WebStateException(se.getMessage(), se.getType(), se.getSource(), e);
        } else {
            return new WebStateException(e);
        }
    }
}
