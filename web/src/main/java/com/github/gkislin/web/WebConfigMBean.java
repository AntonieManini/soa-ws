package com.github.gkislin.web;

import com.github.gkislin.common.config.IConfig;

/**
 * User: gkislin
 * Date: 01.07.2014
 */
public interface WebConfigMBean extends IConfig {
    int getRowNumber();

    void setRowNumber(int rowNumber);

}
