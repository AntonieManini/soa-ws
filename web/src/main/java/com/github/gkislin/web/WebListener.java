package com.github.gkislin.web;

import com.github.gkislin.common.config.ConfigListener;
import com.github.gkislin.mail.MailRemoteService;
import masterj.akka.AkkaLookup;

import javax.servlet.ServletContextEvent;
import java.util.List;

/**
 * User: gkislin
 * Date: 27.02.14
 */

public class WebListener extends ConfigListener {
    private static List<MailRemoteService> nodeList;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        nodeList = AkkaLookup.startMaster("WebMailClient", "master", MailRemoteService.class, new String[]{
//                "akka.tcp://MailService@192.168.1.6:2553/user/ubuntu64",
                "akka.tcp://MailService@192.168.1.34:2552/user/windows"
        });
        super.contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        super.contextDestroyed(sce);
        AkkaLookup.shutdown();
    }

    public static List<MailRemoteService> getNodeList() {
        return nodeList;
    }
}
