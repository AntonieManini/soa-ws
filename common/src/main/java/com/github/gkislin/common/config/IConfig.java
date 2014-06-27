package com.github.gkislin.common.config;

import javax.management.MXBean;

/**
 * User: gkislin
 * Date: 12.09.13
 */
@MXBean
public interface IConfig {
    String toString();

    void reload();

    void set(String key, String value) throws Exception;
}
