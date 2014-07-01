package com.github.gkislin.mail;

import com.github.gkislin.common.Creatable;
import com.github.gkislin.common.converter.Converter;
import com.github.gkislin.common.converter.ConverterUtil;
import com.github.gkislin.common.util.Util;

import javax.activation.URLDataSource;
import java.net.URL;
import java.util.List;
import java.util.Map;

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

    public static final Converter<ByteAttach, MailAttach> BYTE_ATTACH_CONVERTER = new Converter<ByteAttach, MailAttach>() {
        @Override
        public MailAttach convert(ByteAttach byteAttach) throws Exception {
            return new MailAttach(byteAttach.getName(), new org.apache.commons.mail.ByteArrayDataSource(byteAttach.getData(), null), byteAttach.getDescription());
        }
    };

    public static void save(List<MailAttach> attachments) {
        for (MailAttach attach : attachments) {
            attach.save();
        }
    }

    private static final Map<Class<Attach>, Converter<Attach, MailAttach>> CONVERT_MAP =
            Util.asMap(UrlAttach.class, URL_ATTACH_CONVERTER,
                    MimeAttach.class, MIME_ATTACH_CONVERTER,
                    ByteAttach.class, BYTE_ATTACH_CONVERTER);


    public static <T extends Attach> Converter<Attach, MailAttach> getConverter(List<T> attachments) {
        if (Util.isNotEmpty(attachments)) {
            return CONVERT_MAP.get(attachments.get(0).getClass());
        }
        return null;
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
