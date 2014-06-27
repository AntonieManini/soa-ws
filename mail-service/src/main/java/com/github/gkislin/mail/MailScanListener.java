package com.github.gkislin.mail;

import com.github.gkislin.common.ExceptionSource;
import com.github.gkislin.common.schedule.SchedulerListener;
import com.github.gkislin.mail.dao.MailHistoryDAO;

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
        todoScanner.activate(MailConfig.get().scanTODO);
        failScanner.activate(MailConfig.get().scanFail);
    }

    @Override
    public void deactivate() {
        todoScanner.deactivate();
        failScanner.deactivate();
    }
}
