package com.chmihun.searchagent.agents;

import java.util.HashMap;
import java.util.Map;

/**
 * Agents factory that provides all available agents instances
 */
public class AgentFactory {

    private static Map<String, Agent> agents = new HashMap<>();

    public static Agent getAgent(AgentTypes agentType) {
        return agents.containsKey(agentType.getAgentName()) ? agents.get(agentType.getAgentName()) : getNewAgent(agentType);
    }

    private static Agent getNewAgent(AgentTypes agentType) {
        Agent newAgent = null;

        switch (agentType) {
            case GOOGLE:
                newAgent = GoogleSearchAgent.getGoogleSearchAgentInstance();
                break;
            case MAILRU:
                //TODO: add mailru agent instance
                newAgent = null;
                break;
        }

        if (newAgent != null) {
            agents.put(agentType.getAgentName(), newAgent);
        }

        return newAgent;
    }
}
