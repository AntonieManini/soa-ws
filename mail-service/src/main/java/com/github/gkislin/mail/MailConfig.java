package com.github.gkislin.mail;

import com.github.gkislin.common.config.IConfig;
import com.github.gkislin.common.config.RootConfig;
import com.typesafe.config.Config;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.Authenticator;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * User: gkislin
 * Date: 12.09.13
 */
public class MailConfig implements IConfig {
    private static final MailConfig INSTANCE = new MailConfig();

    private String server;
    private int smtpPort;
    private boolean useSSL;
    private boolean useTLS;
    private boolean debug;
    private String charset;
    private String user;
    private String fromName;
    private Authenticator auth;

    long scanTODO, scanFail;
    private int poolSize;

    public static MailConfig get() {
        return INSTANCE;
    }

    public MailConfig() {
        Config conf = RootConfig.get().getSubConfig("mail");
        server = conf.getString("server");
        user = conf.getString("user");
        auth = new DefaultAuthenticator(user, conf.getString("password"));
        fromName = conf.getString("fromName");
        smtpPort = conf.getInt("smtpPort");
        useSSL = conf.getBoolean("useSSL");
        useTLS = conf.getBoolean("useTLS");
        debug = conf.getBoolean("debug");
        charset = conf.getString("charset");

        scanTODO = conf.getDuration("scanTODO", TimeUnit.SECONDS);
        scanFail = conf.getDuration("scanFail", TimeUnit.SECONDS);
        poolSize = conf.getInt("poolSize");
    }

    @Override
    public String toString() {
        return "MailConfig{" +
                "charset='" + charset + '\'' +
                ", server='" + server + '\'' +
                ", user='" + user + '\'' +
                ", fromName='" + fromName + '\'' +
                ", smtpPort=" + smtpPort +
                ", useSSL=" + useSSL +
                ", useTLS=" + useTLS +
                ", debug=" + debug +
                '}';
    }

    public HtmlEmail createEmail() throws EmailException {
        return prepareEmail(new HtmlEmail());
    }


    public <T extends Email> T prepareEmail(T email) throws EmailException {
        email.setFrom(user, fromName);
        email.setHostName(server);
        email.setSmtpPort(smtpPort);
        email.setSSLOnConnect(useSSL);
        email.setStartTLSEnabled(useTLS);
        email.setDebug(debug);
        email.setAuthenticator(auth);
        email.setCharset(charset);
        return email;
    }

    public String encodeWord(String word) throws UnsupportedEncodingException {
        if (word == null) {
            return null;
        }
        return MimeUtility.encodeWord(word, charset, null);
    }
}
