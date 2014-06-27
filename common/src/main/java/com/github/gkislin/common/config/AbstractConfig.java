package com.github.gkislin.common.config;

import com.typesafe.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * User: gkislin
 * Date: 12.09.13
 */
abstract public class AbstractConfig implements IConfig {
    protected Config getConfig(String name) {
        return RootConfig.get().getSubConfig(name);
    }

    abstract protected void init();

    @Override
    public void reload() {
        RootConfig.get().reload();
        init();
    }

    @Override
    public void set(String key, String value) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected int getInSecond(com.typesafe.config.Config conf, String key) {
        return (int) conf.getDuration(key, TimeUnit.SECONDS);
    }

    @Override
    abstract public String toString();

}
