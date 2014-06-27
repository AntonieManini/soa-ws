package com.github.gkislin.mail;

import com.github.gkislin.common.config.AbstractConfig;
import com.github.gkislin.common.config.RootConfig;
import com.github.gkislin.common.io.Directory;
import com.github.gkislin.common.util.AsyncExecutor;
import com.typesafe.config.Config;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.Authenticator;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * User: gkislin
 * Date: 12.09.13
 */
public class MailConfig extends AbstractConfig implements MailConfigMBean{
    private static final MailConfig INSTANCE = new MailConfig();
    private static final String SCAN_TODO = "scanTODO";
    private static final String SCAN_FAIL = "scanFail";
    private static final String POOL_SIZE = "poolSize";

    private volatile MailProps props;
    volatile int scanTODO, scanFail;
    private volatile int poolSize;

    public static File getAttachDir() {
        return INSTANCE.props.attachDir;
    }

    private static class MailProps {
        final private String server;
        final private int smtpPort;
        final private boolean useSSL;
        final private boolean useTLS;
        final private boolean debug;
        final private String charset;
        final private String user;
        final private String fromName;
        final private Authenticator auth;
        final Directory attachDir;

        private MailProps(Config conf) {
            server = conf.getString("server");
            user = conf.getString("user");
            auth = new DefaultAuthenticator(user, conf.getString("password"));
            fromName = conf.getString("fromName");
            smtpPort = conf.getInt("smtpPort");
            useSSL = conf.getBoolean("useSSL");
            useTLS = conf.getBoolean("useTLS");
            debug = conf.getBoolean("debug");
            charset = conf.getString("charset");
            attachDir = new Directory(conf.getString("attachDir"));
        }

        public <T extends Email> T prepareEmail(T email) throws EmailException {
            email.setFrom(user, fromName);
            email.setHostName(server);
            email.setSmtpPort(smtpPort);
            if (useSSL) {
                email.setSslSmtpPort(String.valueOf(smtpPort));
            } else {
                email.setSmtpPort(smtpPort);
            }
            email.setSSLOnConnect(useSSL);
            email.setStartTLSEnabled(useTLS);
            email.setDebug(debug);
            email.setAuthenticator(auth);
            email.setCharset(charset);
            return email;
        }

        @Override
        public String toString() {
            return "\nserver='" + server + '\'' +
                    "\nsmtpPort=" + smtpPort +
                    "\nuseSSL=" + useSSL +
                    "\nuseTLS=" + useTLS +
                    "\ndebug=" + debug +
                    "\ncharset='" + charset + '\'' +
                    "\nuser='" + user + '\'' +
                    "\nfromName='" + fromName + '\'' +
                    "\nattachDir=" + attachDir;
        }
    }


    public static MailConfig get() {
        return INSTANCE;
    }

    private MailConfig() {
        init();
    }

    @Override
    protected void init() {
        Config conf = RootConfig.get().getSubConfig("mail");
        props = new MailProps(conf);

        setPoolSize(conf.getInt(POOL_SIZE));
        setScanTODO(getInSecond(conf, SCAN_TODO));
        setScanFail(getInSecond(conf, SCAN_FAIL));
    }

    public void setScanTODO(int inSecond) {
        scanTODO = inSecond;
        MailScanListener.<MailScanListener>getScheduler().todoScanner.activate(scanTODO);
    }

    @Override
    public int getScanFail() {
        return scanFail;
    }

    public void setScanFail(int inSecond) {
        scanFail = inSecond;
        MailScanListener.<MailScanListener>getScheduler().failScanner.activate(scanFail);
    }

    @Override
    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
        AsyncExecutor.setPoolSize(poolSize);
    }

    @Override
    public int getScanTODO() {
        return scanTODO;
    }

    @Override
    public void set(String key, String value) throws Exception {
        int val = Integer.valueOf(value);
        switch (key) {
            case POOL_SIZE:
                setPoolSize(val);
                break;
            case SCAN_TODO:
                setScanTODO(val);
                break;
            case SCAN_FAIL:
                setScanFail(val);
                break;
            default:
                super.set(key, value);
        }
    }

    @Override
    public String toString() {
        return "host=" + RootConfig.get().getHost("mail") +
                "\nscanTODO=" + scanTODO +
                "\nscanFail=" + scanFail +
                "\npoolSize=" + poolSize +
                props.toString();
    }

    public static HtmlEmail createEmail() throws EmailException {
        return INSTANCE.props.prepareEmail(new HtmlEmail());
    }

    public static String encodeWord(String word) throws UnsupportedEncodingException {
        if (word == null) {
            return null;
        }
        return MimeUtility.encodeWord(word, INSTANCE.props.charset, null);
    }
}
