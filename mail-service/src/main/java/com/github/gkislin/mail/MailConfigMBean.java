package com.github.gkislin.mail;


import com.github.gkislin.common.config.IConfig;

/**
 * User: gkislin
 * Date: 19.02.14
 */
public interface MailConfigMBean extends IConfig {
    int getPoolSize();
    void setPoolSize(int poolSize);

    int getScanTODO();
    void setScanTODO(int sec);

    int getScanFail();
    void setScanFail(int sec);

}