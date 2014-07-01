package com.github.gkislin.web;

import com.github.gkislin.common.config.AbstractConfig;
import com.github.gkislin.common.config.RootConfig;
import com.github.gkislin.mail.MailWSClient;
import com.typesafe.config.Config;

/**
 * User: gkislin
 * Date: 01.07.2014
 */
public class WebConfig extends AbstractConfig implements WebConfigMBean {
    private static final WebConfig INSTANCE = new WebConfig();
    private static final String ROW_NUMBER = "rowNumber";

    public volatile int rowNumber;

    public static WebConfig get() {
        return INSTANCE;
    }

    public WebConfig() {
        init();
    }

    @Override
    public int getRowNumber() {
        return rowNumber;
    }

    @Override
    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    @Override
    protected void init() {
        Config conf = getConfig("web");
        rowNumber = conf.getInt(ROW_NUMBER);
        MailWSClient.init();
    }

    @Override
    public void set(String key, String value) throws Exception {
        if (ROW_NUMBER.equals(key)) {
            rowNumber = Integer.valueOf(value);
        } else {
            super.set(key, value);
        }
    }

    @Override
    public String toString() {
        return "mailService host=" + RootConfig.get().getHost("mail") +
                "\nrowNumber=" + rowNumber+"\n";
    }
}
