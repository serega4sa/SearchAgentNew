package com.chmihun.searchagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Sergey.Chmihun on 05/10/2017.
 */
public class MyServletContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(MyServletContextListener.class.getName());
    private Thread tServer;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Server server = new Server();
        Server.setServer(server);
        tServer = new Thread(server);
        tServer.start();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        tServer.interrupt();
    }
}
