package com.chmihun.searchagent;

import com.chmihun.searchagent.agents.AgentFactory;
import com.chmihun.searchagent.agents.AgentTypes;
import com.chmihun.searchagent.agents.GoogleSearch;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created by Sergey.Chmihun on 07/03/2017.
 */
@WebServlet(name = "ActionHandlerServlet", urlPatterns = {"/action"})
public class ActionHandlerServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ActionHandlerServlet.class.getName());
    private GoogleSearch gs;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObjectInput;

        try {
            jsonObjectInput = (JSONObject) parser.parse(request.getReader());

            if (jsonObjectInput != null) {
                logger.debug("Incoming json: " + jsonObjectInput.toJSONString());

                boolean isSuccessful = performAction(jsonObjectInput);

                JSONObject jsonObjectOutput = new JSONObject();
                jsonObjectOutput.put("result", isSuccessful ? 1 : 0);

                response.setContentType("application/json");
                response.getWriter().write(jsonObjectOutput.toString());
                logger.debug("Outgoing json: " + jsonObjectOutput.toString());
            }
        } catch (ParseException e) {
            logger.error("Problems with parsing json input. ", e);
        } catch (IOException e) {
            JSONObject jsonObjectOutput = new JSONObject();
            jsonObjectOutput.put("result", 0);
            response.setContentType("application/json");
            response.getWriter().write(jsonObjectOutput.toString());
            logger.error("Outgoing json: " + jsonObjectOutput.toString() + ". Something went wrong: " + e);
        }
    }

    private boolean performAction(JSONObject jsonObjectInput) {
        String actionName = ((String) jsonObjectInput.get("action"));
        if (actionName.equals("getGoogleSearchResults")) {
            generateGoogleSearchResults(jsonObjectInput);
            return true;
        } else if (actionName.equals("getStatistics")) {
            gs = (GoogleSearch) AgentFactory.getAgent(AgentTypes.GOOGLE);
            String webappPath = getServletContext().getRealPath(File.separator);
            return gs.generateStatisticsForPeriod(jsonObjectInput.get("query").toString(), jsonObjectInput.get("startDate").toString(), jsonObjectInput.get("endDate").toString(), webappPath);
        }
        return false;
    }

    private void generateGoogleSearchResults(JSONObject jsonObjectInput) {
        gs = (GoogleSearch) AgentFactory.getAgent(AgentTypes.GOOGLE);
        gs.getListOfRequests().clear();
        gs.getListOfRequests().add(jsonObjectInput.get("query").toString());
        gs.setvDuration(jsonObjectInput.get("vDuration").toString());
        gs.setqDuration(jsonObjectInput.get("qDuration").toString());
        gs.setLocalization(jsonObjectInput.get("localization").toString());
        gs.setNumberOfPages(Integer.parseInt(jsonObjectInput.get("numOfPages").toString()));
        gs.run();
    }
}
