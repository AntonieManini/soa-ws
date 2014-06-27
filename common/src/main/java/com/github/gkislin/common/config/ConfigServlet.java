package com.github.gkislin.common.config;

import com.github.gkislin.common.util.ParamUtil;
import com.github.gkislin.common.web.CommonServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * User: gkislin
 * Date: 25.02.14
 */
public class ConfigServlet extends CommonServlet {

    @Override
    protected void doProcess(HttpServletRequest request, HttpServletResponse response, Map<String, String> params) throws IOException, ServletException {
//        boolean reload = ConfigFactory.parseMap(params).getBoolean("reload");

        IConfig config = ConfigListener.getConfig();

        if (ParamUtil.getBoolean("reload", params, false)) {
            config.reload();

        } else if (!params.isEmpty()) {
            Map.Entry<String, String> entry = params.entrySet().iterator().next();
            try {
                config.set(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                throw new IllegalArgumentException("Illegal params " + entry.getKey() + "='" + entry.getValue() + "'", e);
            }
        }
        setResponse(response, config.toString());
    }
}
