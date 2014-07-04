package com.github.gkislin.web;

import com.github.gkislin.common.ExceptionType;
import com.github.gkislin.common.converter.ConverterUtil;
import com.github.gkislin.common.web.CommonServlet;
import com.github.gkislin.common.web.HtmlUtil;
import com.github.gkislin.mail.Addressee;
import com.github.gkislin.mail.MailRemoteService;
import com.github.gkislin.mail.MailWSClient;
import com.github.gkislin.mail.UrlAttach;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * User: gkislin
 * Date: 23.01.14
 */
public class SendMailServlet extends CommonServlet {

    @Override
    protected void doProcess(HttpServletRequest request, HttpServletResponse response, Map<String, String> params) throws IOException, ServletException {
//            MailWSClient.sendMail(params.get("to"), params.get("cc"), params.get("subject"), params.get("body"));
        sendMail(request, response, params, false);
    }

    protected void sendMail(HttpServletRequest request, HttpServletResponse response, Map<String, String> params, boolean async) throws IOException {
//        MailWSClient.sendMail(params.get("to"), params.get("cc"), params.get("subject"), params.get("body"));
        try {
            List<FileItem> attachments = new LinkedList<>();
            if (ServletFileUpload.isMultipartContent(request)) {
                try {
                    FileItemFactory factory = new DiskFileItemFactory();
                    List<FileItem> fileItems = new ServletFileUpload(factory).parseRequest(request);
                    for (FileItem fileItem : fileItems) {
                        if (fileItem.isFormField()) {
                            params.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
                        } else {
                            attachments.add(fileItem);
                        }
                    }
                } catch (FileUploadException e) {
                    throw logger.getStateException("Ошибка загрузки файла", ExceptionType.ATTACH, e);
                }


                List<Addressee> to = fromParam(params, "to");
                List<Addressee> cc = fromParam(params, "cc");
                String subject = params.get("subject");
                String body = params.get("body");
                List<UrlAttach> urlAttach = ConverterUtil.convert(attachments, FileItemAttachConverters.FILE_ITEM_URL_CONVERTER, ExceptionType.ATTACH);

                if ("ws".equals(params.get("transport"))) {
                    if ("urlType".equals(params.get("type"))) {
                        MailWSClient.sendMailUrl(to, cc, subject, body, urlAttach, false);
                    } else {
                        MailWSClient.sendMailMime(to, cc, subject, body,
                                ConverterUtil.convert(attachments, FileItemAttachConverters.FILE_ITEM_MIME_CONVERTER, ExceptionType.ATTACH), false);
                    }
                } else {
                    MailRemoteService akkaItf = WebListener.getNodeList().get(0);
                    akkaItf.sendMail(to, cc, subject, body, "urlType".equals(params.get("type")) ?
                            urlAttach : ConverterUtil.convert(attachments, FileItemAttachConverters.FILE_ITEM_BYTE_CONVERTER, ExceptionType.ATTACH));
                }
            }
        } catch (Exception e) {
            logger.error(e);
            setResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, HtmlUtil.toHtml(e));
        }

        response.sendRedirect(request.getContextPath());
    }

    private List<Addressee> fromParam(Map<String, String> params, String key) {
        return MailWSClient.create(params.get(key));
    }
}
