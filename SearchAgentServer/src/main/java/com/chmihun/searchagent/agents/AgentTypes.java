package com.chmihun.searchagent.agents;

public enum AgentTypes {

    GOOGLE("google"),
    MAILRU("mailru");

    private String agentName;

    AgentTypes(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentName() {
        return agentName;
    }
}
