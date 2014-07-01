package com.github.gkislin.mail.dao;

import com.github.gkislin.common.sql.Sql;
import com.github.gkislin.mail.Addressee;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * User: gkislin
 * Date: 26.09.13
 */
public class MailHistoryDAO {

    public static final String TODO_STATE = "TODO";

    private static final BeanListHandler<MailHist> MAIL_HIST_HANDLER = new BeanListHandler<>(MailHist.class);
    private static final BeanHandler<MailHist> MAIL_HANDLER = new BeanHandler<>(MailHist.class);

    public static void save(List<Addressee> to, List<Addressee> cc, String subject, String body, String state, Collection attachList) throws SQLException {
        Sql.update("INSERT INTO mail_hist(list_to,  list_cc,  subject,  body,  state,  date, attach_list)" +
                " VALUES (?,?,?,?,?,now(),?)", StringUtils.join(to, ", "), StringUtils.join(cc, ", "), subject, body, state, StringUtils.join(attachList, ", "));
    }

    public static List<MailHist> getAll() {
        return Sql.query("SELECT id, list_to as listTo, list_cc as listCc,subject,state,date FROM mail_hist ORDER BY date DESC", MAIL_HIST_HANDLER);
    }

    public static MailHist get(int id) {
        return Sql.query("SELECT id,list_to as listTo,list_cc as listCc,subject,state,date,body FROM mail_hist where id=?", MAIL_HANDLER, id);
    }

    public static List<MailHist> getAllWithState(String startWith) {
        return Sql.query("SELECT id,list_to as listTo,list_cc as listCc,subject,state,date,body,attach_list as attachList FROM mail_hist where state like '" + startWith + "%'", MAIL_HIST_HANDLER);
    }

    public static void updateState(int id, String state) {
        Sql.update("UPDATE mail_hist SET state=? where id=?", state, id);
    }

    public static List<MailHist> getLast(int rowNumber) {
        return Sql.query("SELECT id,list_to as listTo,list_cc as listCc,subject,state,date FROM mail_hist ORDER BY date DESC LIMIT " + rowNumber, MAIL_HIST_HANDLER);
    }
}
