package com.github.gkislin.common.config;


import com.github.gkislin.common.LoggerWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: gkislin
 * Date: 18.02.14
 */

public class ProjectInfo {
    private static final LoggerWrapper LOGGER = LoggerWrapper.get(ProjectInfo.class);

    public final String projectName, buildNum, version, groupId, appName;

    public ProjectInfo() {
        try (InputStream is = ProjectInfo.class.getResourceAsStream("/app.properties")) {
            Properties props = new Properties();
            props.load(is);
            groupId = props.getProperty("groupId");
            projectName = props.getProperty("projectName");
            version = props.getProperty("version");
            buildNum = props.getProperty("buildNum");
            appName = groupId + '.' + projectName;
        } catch (IOException e) {
            throw LOGGER.getIllegalStateException("app.properties not found as classpath resource", e);
        }
    }

    @Override
    public String toString() {
        return "appName='" + appName + '\'' +
                "\nversion='" + version + '\'' +
                "\nbuildNum='" + buildNum + '\'';
    }
}
