package com.github.gkislin.mail;

import com.github.gkislin.common.StateException;
import com.github.gkislin.common.schedule.AsyncTask;
import com.github.gkislin.common.util.AsyncExecutor;
import com.github.gkislin.mail.dao.MailHist;
import com.github.gkislin.mail.dao.MailHistoryDAO;

import java.util.List;

/**
 * User: gkislin
 * Date: 19.06.2014
 */
public class MailScanTask extends AsyncTask {
    private final String startWith;

    public MailScanTask(String startWith) {
        this.startWith = startWith;
    }

    public void activate(long scanPeriod) {
        activate(new Runnable() {

            @Override
            public void run() {
                List<MailHist> mailToSend = MailHistoryDAO.getAllWithState(startWith);
                if (!mailToSend.isEmpty()) {
                    logger.info("Found " + mailToSend.size() + " mail with state=" + startWith);
                    boolean first = true;
                    for (final MailHist mail : mailToSend) {
                        if (first) {
                            try {
                                MailSender.sendMail(mail);
                            } catch (StateException e) {
                                if (MailScanListener.REPEAT_CAUSE == e.getSource()) {
                                    break;
                                }
                            }
                            first = false;
                        } else {
                            AsyncExecutor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    MailSender.sendMail(mail);
                                }
                            });
                        }
                    }
                }
            }
        }, scanPeriod, false);
    }
}
