package com.github.gkislin.common.web;

import com.github.gkislin.common.LoggerWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * User: gkislin
 * Date: 22.01.14
 */
public abstract class CommonServlet extends HttpServlet {
    protected final LoggerWrapper logger = LoggerWrapper.get(getClass());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        logger.info(ServletUtil.toString(request) + " received");

        Enumeration params = request.getParameterNames();
        HashMap<String, String> map = new HashMap<String, String>();
        while (params.hasMoreElements()) {
            String key = (String) params.nextElement();
            map.put(key, request.getParameter(key));
        }
        doProcess(request, response, map);
    }

    protected abstract void doProcess(HttpServletRequest request, HttpServletResponse response, Map<String, String> params) throws IOException, ServletException;

}
