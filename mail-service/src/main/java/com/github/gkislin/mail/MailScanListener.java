package com.github.gkislin.mail;

import akka.japi.Creator;
import com.github.gkislin.common.ExceptionSource;
import com.github.gkislin.common.StateException;
import com.github.gkislin.common.converter.Converter;
import com.github.gkislin.common.schedule.SchedulerListener;
import com.github.gkislin.mail.dao.MailHistoryDAO;
import masterj.akka.AkkaLookup;

import java.util.List;

/**
 * User: gkislin
 * Date: 10.02.14
 */
public class MailScanListener extends SchedulerListener {
    static final ExceptionSource REPEAT_CAUSE = ExceptionSource.NETWORK;

    final MailScanTask todoScanner = new MailScanTask(MailHistoryDAO.TODO_STATE);
    final MailScanTask failScanner = new MailScanTask(REPEAT_CAUSE.name());


    @Override
    public void activate() {
        String nodeName = System.getProperty("nodeName");
        if (nodeName == null) {
            throw logger.getIllegalStateException("Run with JVM param -DnodeName=...");
        }

        AkkaLookup.startNode("MailService", nodeName, MailRemoteService.class, new Creator<MailRemoteService>() {
            @Override
            public MailRemoteService create() throws Exception {
                return new MailRemoteService() {
                    @Override
                    public void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body, List<? extends Attach> attachments) throws StateException {
                        MailSender.sendMail(to, cc, subject, body, attachments, (Converter) AttachHelper.getConverter(attachments));
                    }
                };
            }
        });
        todoScanner.activate(MailConfig.get().scanTODO);
        failScanner.activate(MailConfig.get().scanFail);
    }

    @Override
    public void deactivate() {
        todoScanner.deactivate();
        failScanner.deactivate();
        AkkaLookup.shutdown();
    }
}
