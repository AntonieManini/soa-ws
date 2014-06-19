package com.github.gkislin.mail;

import com.github.gkislin.common.config.RootConfig;
import com.github.gkislin.common.util.AsyncExecutor;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * User: gkislin
 * Date: 28.01.14
 */
public class MailWSClientIT {
    private static final UrlAttach ATTACH;
    private static final List<Addressee> ADDRESSEE_LIST;

    static {
        ATTACH = new UrlAttach("Одуванчик.jpg", MailWSClientIT.class.getClassLoader().getResource("Одуванчик.jpg").toString());
        ADDRESSEE_LIST = MailWSClient.create("Имя <gkislin@yandex.ru>");
    }

    @Test
    public void testSendMail() throws Exception {
        MailWSClient.setCredential(
                RootConfig.getConf().getString("mail.client.user"),
                RootConfig.getConf().getString("mail.client.password"));

        MailWSClient.sendMailUrl(ADDRESSEE_LIST, null, "Русский текст", "<h2>Боди</h2>", Collections.singletonList(
                ATTACH), false);
    }

    @Test
    public void testSendMailAsync() throws Exception {
        MailWSClient.setCredential(
                RootConfig.getConf().getString("mail.client.user"),
                RootConfig.getConf().getString("mail.client.password"));

        Future<?> future = MailWSClient.sendMailUrl(ADDRESSEE_LIST, null, "Аснихронная отправка почты", "<h2>Боди</h2>", Collections.singletonList(
                ATTACH), true);
        AsyncExecutor.waitFuture(future);
    }

    @Test
    public void testNoAuthSendMail() throws Exception {
        MailWSClient.setCredential(null, null);

        try {
            MailWSClient.sendMailUrl(ADDRESSEE_LIST, null, "Русский текст", "<h2>Боди</h2>", Collections.singletonList(
                    ATTACH), false);
        } catch (Exception e) {
            org.junit.Assert.assertTrue(e.getMessage().contains("401"));
            return;
        }
        org.junit.Assert.fail();
    }

    @Test
    public void testWrongAuthSendMail() throws Exception {
        MailWSClient.setCredential("dummy", "dummy");

        try {
            MailWSClient.sendMailUrl(ADDRESSEE_LIST, null, "Русский текст", "<h2>Боди</h2>", Collections.singletonList(
                    ATTACH), false);
        } catch (Exception e) {
            org.junit.Assert.assertTrue(e.getMessage().contains("403"));
            return;
        }
        org.junit.Assert.fail();
    }
}
