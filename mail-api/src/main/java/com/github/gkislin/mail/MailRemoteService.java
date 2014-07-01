package com.github.gkislin.mail;

import com.github.gkislin.common.StateException;

import java.util.List;

public interface MailRemoteService {

    public void sendMail(
            List<Addressee> to,
            List<Addressee> cc,
            String subject,
            String body,
            List<? extends Attach> attachments) throws StateException;
}