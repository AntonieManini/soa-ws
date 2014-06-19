package com.github.gkislin.mail;

import com.github.gkislin.common.Creatable;
import com.github.gkislin.common.converter.Converter;
import com.github.gkislin.common.converter.ConverterUtil;

import javax.activation.URLDataSource;
import java.net.URL;
import java.util.List;

/**
 * User: gkislin
 * Date: 16.09.13
 */
public class AttachHelper {

    public static final Converter<UrlAttach, MailAttach> URL_ATTACH_CONVERTER = new Converter<UrlAttach, MailAttach>() {
        @Override
        public MailAttach convert(UrlAttach attach) throws Exception {
            return new MailAttach(attach.getName(), new URLDataSource(new URL(attach.getUrl())), attach.getDescription());
        }
    };

    public static final Converter<MimeAttach, MailAttach> MIME_ATTACH_CONVERTER = new Converter<MimeAttach, MailAttach>() {
        @Override
        public MailAttach convert(MimeAttach mimeAttach) throws Exception {
            return new MailAttach(mimeAttach.getName(), mimeAttach.dataHandler.getDataSource(), mimeAttach.getDescription());
        }
    };

    public static void save(List<MailAttach> attachments) {
        for (MailAttach attach : attachments) {
            attach.save();
        }
    }

    public static List<MailAttach> create(String attachList) {
        return ConverterUtil.create(attachList, ",", new Creatable<MailAttach>() {
            @Override
            public MailAttach create(String attach) {
                return new MailAttach(attach);
            }
        });
    }

    public static void delete(List<MailAttach> attachments) {
        for (MailAttach attach : attachments) {
            attach.delete();
        }
    }
}
