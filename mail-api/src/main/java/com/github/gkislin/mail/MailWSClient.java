package com.github.gkislin.mail;


import com.github.gkislin.common.LoggerWrapper;
import com.github.gkislin.common.LoggingLevel;
import com.github.gkislin.common.StateException;
import com.github.gkislin.common.config.RootConfig;
import com.github.gkislin.common.util.AsyncExecutor;
import com.github.gkislin.common.util.Util;
import com.github.gkislin.common.web.ServletUtil;
import com.github.gkislin.common.web.WebStateException;
import com.github.gkislin.common.web.WsClient;
import com.github.gkislin.common.web.handler.SoapClientLoggingHandler;
import org.apache.commons.lang.StringUtils;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * User: gkislin
 * Date: 06.08.12
 */
public class MailWSClient {

    private static final WsClient<MailService> WS_CLIENT;
    private static final LoggerWrapper LOGGER = LoggerWrapper.get(MailWSClient.class);
    private static final SoapClientLoggingHandler LOGGING_HANDLER = new SoapClientLoggingHandler(LoggingLevel.DEBUG);

    private static String user, password;
    private static String authHeader;

    static {
        WS_CLIENT = new WsClient<>(
                "wsdl/mailService.wsdl",
                new QName("http://mail.gkislin.github.com/", "MailServiceImplService"),
                MailService.class);
        setHost(RootConfig.get().getHost("mail"));
        setCredential(
                RootConfig.getConf().getString("mail.client.user"),
                RootConfig.getConf().getString("mail.client.password"));
    }

    public static void setCredential(String user, String password) {
        MailWSClient.user = user;
        MailWSClient.password = password;
        MailWSClient.authHeader = ServletUtil.encodeBasicAuthHeader(user, password);
    }

    public static void setHost(String host) {
        WS_CLIENT.setEndpointAddress(host + "/mail/mailService?wsdl");
    }

    // Get from "Name <mail>" or "mail"
    public static void sendMail(String to, String cc, String subject, String body, boolean async) throws StateException {
        sendMail(create(to), create(cc), subject, body, async);
    }

    public static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body, boolean async) throws StateException {
        sendMailUrl(to, cc, subject, body, null, async);
    }

    public static Future<?> sendMailUrl(final List<Addressee> to, final List<Addressee> cc, final String subject, final String body, final List<UrlAttach> attachments, boolean async) throws StateException {
        LOGGER.info("Send mail to '" + to + "' cc '" + cc + "' subject '" + subject + (LOGGER.isDebug() ? "\nbody=" + body : ""));
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    getPort().sendMailUrl(to, cc, subject, body, attachments);
                } catch (WebStateException e) {
                    throw LOGGER.getStateException(e);
                }
            }
        };
        return AsyncExecutor.submit(task, async);
    }

    public static void sendMailMime(final List<Addressee> to, final List<Addressee> cc, final String subject, final String body, final List<MimeAttach> attachments, boolean async) throws StateException {
        LOGGER.info("Send mail to '" + to + "' cc '" + cc + "' subject '" + subject + (LOGGER.isDebug() ? "\nbody=" + body : ""));
        AsyncExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    getPort().sendMailMime(to, cc, subject, body, attachments);
                } catch (WebStateException e) {
                    throw LOGGER.getStateException(e);
                }
            }
        }, async);
    }

    private static MailService getPort() {
        MailService port = WS_CLIENT.getPort();
        WsClient.setHandler(port, LOGGING_HANDLER);
        if (user != null && password != null) {
            WsClient.setAuth(port, user, password);
        }
        return port;
    }

    public static List<Addressee> create(String emails) {
        if (Util.isEmpty(emails)) {
            return Collections.emptyList();
        }

        // Question: http://stackoverflow.com/questions/6374050/string-split-not-on-regular-expression
        if (emails.contains(",")) {
//            emails.split(",");
            String[] array = StringUtils.split(emails, ',');

            List<Addressee> list = new ArrayList<>(array.length);
            for (String email : array) {
                list.add(new Addressee(email));
            }
            return list;
        } else {
            return Collections.singletonList(new Addressee(emails));
        }
    }

    public static String getAuthHeader() {
        return authHeader;
    }
}
