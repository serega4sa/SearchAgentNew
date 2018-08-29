package com.chmihun.searchagent;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Sergey.Chmihun on 05/10/2017.
 */
public class MyServletContextListener implements ServletContextListener {

    private Thread tServer;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        tServer = new Thread(Server.getServerInstance());
        tServer.start();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        tServer.interrupt();
    }
}
