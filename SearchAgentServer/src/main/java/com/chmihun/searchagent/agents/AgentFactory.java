package com.chmihun.searchagent.agents;

/**
 * Created by Sergey.Chmihun on 08/23/2017.
 */
public class AgentFactory {
    public static Agent getAgent(String criteria) {
        if (criteria.equals("google")) {
            return new GoogleSearch();
        } else if (criteria.equals("mailru")) {
            return null;
        }

        return null;
    }
}
