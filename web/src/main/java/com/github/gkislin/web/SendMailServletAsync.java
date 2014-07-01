package com.github.gkislin.web;

import com.github.gkislin.common.util.AsyncExecutor;
import com.github.gkislin.common.web.HtmlUtil;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * User: gkislin
 * Date: 01.07.2014
 */

@WebServlet(asyncSupported = true, value = "/sendMailAsync")
public class SendMailServletAsync extends SendMailServlet{
    @Override
    protected void doProcess(final HttpServletRequest request, final HttpServletResponse response, final Map<String, String> params) throws IOException, ServletException {
        final AsyncContext ac = request.startAsync(); // obtain async context
        Runnable worker = new Runnable() {
            @Override
            public void run() {
                try {
                    sendMail(request, response, params, false);
                } catch (Exception e) {
                    try {
                        setResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, HtmlUtil.toHtml(e));
                    } catch (IOException e1) {
                        logger.error(e);
                    }
                } finally {
                    ac.complete();
                    logger.info("Async request finish");
                }
            }
        };
        AsyncExecutor.execute(worker);
// Delegate to the container
//        ac.start(worker);
        logger.info("Async request in progress");
    }
}
