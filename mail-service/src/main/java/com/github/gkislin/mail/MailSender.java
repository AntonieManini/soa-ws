package com.github.gkislin.mail;

import com.github.gkislin.common.ExceptionType;
import com.github.gkislin.common.LoggerWrapper;
import com.github.gkislin.common.StateException;
import com.github.gkislin.common.converter.Converter;
import com.github.gkislin.common.converter.ConverterUtil;
import com.github.gkislin.common.util.Util;
import com.github.gkislin.mail.dao.MailHist;
import com.github.gkislin.mail.dao.MailHistoryDAO;
import org.apache.commons.mail.HtmlEmail;

import java.sql.SQLException;
import java.util.List;

/**
 * User: gkislin
 * Date: 12.12.12
 */
public class MailSender {
    private static final LoggerWrapper LOGGER = LoggerWrapper.get(MailSender.class);

    public static void sendMail(MailHist mail) {
        LOGGER.info("Send mailId=" + mail.getId());
        List<MailAttach> attachments = AttachHelper.create(mail.getAttachList());
        sendMail(MailWSClient.create(mail.getListTo()), MailWSClient.create(mail.getListCc()), mail.getSubject(), mail.getBody(), attachments, mail.getId());
        AttachHelper.delete(attachments);
    }

    static <T extends Attach> void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body, List<T> attachments,
                                            Converter<T, MailAttach> converter) throws StateException {
        sendMail(to, cc, subject, body, ConverterUtil.convert(attachments, converter, ExceptionType.ATTACH), 0);
    }

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body, List<MailAttach> attachments, int id) throws StateException {
        LOGGER.info("Send mail to '" + to + "' cc '" + cc + "' subject '" + subject + (LOGGER.isDebug() ? "\nbody=" + body : ""));
        try {
            HtmlEmail email = MailConfig.get().createEmail();
            email.setSubject(subject);
            email.setHtmlMsg(body);
            if (Util.isNotEmpty(to)) {
                for (Addressee addressee : to) {
                    email.addTo(addressee.getEmail(), addressee.getName());
                }
            }
            if (Util.isNotEmpty(cc)) {
                for (Addressee addressee : cc) {
                    email.addCc(addressee.getEmail(), addressee.getName());
                }
            }
            if (Util.isNotEmpty(attachments)) {
                for (MailAttach ma : attachments) {
                    email.attach(ma.getDataSource(), MailConfig.get().encodeWord(ma.getName()), MailConfig.get().encodeWord(ma.getDescription()));
                }
            }
            email.send();
            if (id == 0) {
                MailHistoryDAO.save(to, cc, subject, body, "OK", attachments);
            } else {
                MailHistoryDAO.updateState(id, "OK");
            }

        } catch (SQLException se) {
            throw LOGGER.getStateException("Ошибка записи в историю", ExceptionType.DATA_BASE, se);

        } catch (Exception e) {
            StateException se = LOGGER.getStateException(ExceptionType.EMAIL, e);
            String state = ((se.getSource() != null) ? se.getSource().name() + ": " : "") + e.toString();
            try {
                if (id == 0) {
                    AttachHelper.save(attachments);
                    MailHistoryDAO.save(to, cc, subject, body, state, attachments);
                } else {
                    MailHistoryDAO.updateState(id, state);
                }
            } catch (SQLException sqle) {
                LOGGER.error(sqle);
            }
            throw se;
        }
    }
}

