package com.github.gkislin.mail.dao;

import com.github.gkislin.common.sql.SqlTest;
import com.github.gkislin.mail.Addressee;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class MailHistoryDAOTest {
    private static final Addressee ADDRESSEE = new Addressee("Имя", "name@mail.ru");

    static {
        SqlTest.initDb();
    }

    @Test
    public void testDAO() throws Exception {
        MailHistoryDAO.save(Collections.singletonList(ADDRESSEE), null, "Заголовок", "Сожержание", "OK", null);
        List<MailHist> hist = MailHistoryDAO.getAll();
        System.out.println(hist);
    }
}