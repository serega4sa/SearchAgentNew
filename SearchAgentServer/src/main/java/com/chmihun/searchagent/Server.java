package com.chmihun.searchagent;

import com.chmihun.searchagent.agents.AgentFactory;
import com.chmihun.searchagent.agents.AgentTypes;
import com.chmihun.searchagent.agents.GoogleSearchAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey.Chmihun on 08/23/2017.
 */
public class Server implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(Server.class.getName());
    private static final Server server = new Server();

    private static Map<String, Thread> listOfThreads = new HashMap<>();

    private Server() {
    }

    public static Server getServerInstance() {
        return server;
    }

    public void run() {
        logger.debug("Server started.");
        listOfThreads.put("googleThread", new Thread((GoogleSearchAgent) AgentFactory.getAgent(AgentTypes.GOOGLE)));
        listOfThreads.get("googleThread").start();
    }
}
