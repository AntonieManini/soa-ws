package com.github.gkislin.common.config;

import com.github.gkislin.common.io.ReadableFile;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * User: gkislin
 * Date: 11.09.13
 */
public class RootConfig extends AbstractConfig {
    private static final RootConfig INSTANCE = new RootConfig();
    private static final String APP_CONF = "application.conf";

    private volatile Config config;
    private volatile Config hosts;
    private final ProjectInfo projectInfo = new ProjectInfo();

    private RootConfig() {
        init();
    }

    public static ProjectInfo getProjectInfo() {
        return INSTANCE.projectInfo;
    }

    @Override
    protected void init() {
        config = ConfigFactory.parseFile(ReadableFile.getResource(APP_CONF)).resolve();
        hosts = config.getConfig("host");
    }

    @Override
    public void reload() {
        init();
    }

    public static RootConfig get() {
        return INSTANCE;
    }

    public static Config getConf() {
        return INSTANCE.config;
    }

    public Config getSubConfig(String path) {
        return config.getConfig(path);
    }

    public String getHost(String name) {
        return hosts.getString(name);
    }

    @Override
    public String toString() {
        return hosts.toString();
    }
}
