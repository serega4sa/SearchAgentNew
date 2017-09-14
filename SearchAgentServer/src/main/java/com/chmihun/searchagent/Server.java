package com.chmihun.searchagent;

import com.chmihun.searchagent.agents.Agent;
import com.chmihun.searchagent.agents.AgentFactory;
import com.chmihun.searchagent.agents.GoogleSearch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey.Chmihun on 08/23/2017.
 */
public class Server implements Runnable{
    private static Server server;
    private static Map<String, Agent> listOfAgents = new HashMap<String, Agent>();
    private static Map<String, Thread> listOfThreads = new HashMap<String, Thread>();

    public static Server getServer() {
        return server;
    }

    public static void setServer(Server server) {
        Server.server = server;
    }

    public static Map<String, Agent> getListOfAgents() {
        return listOfAgents;
    }

    public void run() {
        listOfAgents.put("google", AgentFactory.getAgent("google"));
        listOfThreads.put("googleThread", new Thread((GoogleSearch) listOfAgents.get("google")));
        listOfThreads.get("googleThread").start();
    }
}
