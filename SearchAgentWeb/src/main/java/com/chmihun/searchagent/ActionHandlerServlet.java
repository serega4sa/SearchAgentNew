package com.chmihun.searchagent;

import com.chmihun.searchagent.agents.AgentFactory;
import com.chmihun.searchagent.agents.AgentTypes;
import com.chmihun.searchagent.agents.ExportController;
import com.chmihun.searchagent.agents.GoogleSearchAgent;
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObjectInput;

        try {
            jsonObjectInput = (JSONObject) parser.parse(request.getReader());

            if (jsonObjectInput != null) {
                logger.debug("Incoming json: " + jsonObjectInput.toJSONString());

                boolean isSuccessful = performAction(jsonObjectInput);

                JSONObject jsonObjectOutput = new JSONObject();
                jsonObjectOutput.put(Keys.Response.RESULT, isSuccessful ? 1 : 0);

                response.setContentType("application/json");
                response.getWriter().write(jsonObjectOutput.toString());
                logger.debug("Outgoing json: " + jsonObjectOutput.toString());
            }
        } catch (ParseException e) {
            logger.error("Problems with parsing json input. ", e);
        } catch (IOException e) {
            JSONObject jsonObjectOutput = new JSONObject();
            jsonObjectOutput.put(Keys.Response.RESULT, 0);
            response.setContentType("application/json");
            response.getWriter().write(jsonObjectOutput.toString());
            logger.error("Outgoing json: " + jsonObjectOutput.toString() + ". Something went wrong: " + e);
        }
    }

    private boolean performAction(JSONObject jsonObjectInput) {
        String actionName = ((String) jsonObjectInput.get(Keys.Response.ACTION));
        if (actionName.equals(Keys.Response.GET_GOOGLE_SEARCH_RESULTS)) {
            generateGoogleSearchResults(jsonObjectInput);
            return true;
        } else if (actionName.equals(Keys.Response.GET_STATISTICS)) {
            String query = jsonObjectInput.get(Keys.Response.QUERY).toString();
            String startDate = jsonObjectInput.get(Keys.Response.START_DATE).toString();
            String endDate = jsonObjectInput.get(Keys.Response.END_DATE).toString();
            String webappPath = getServletContext().getRealPath(File.separator);
            return ExportController.generateStatisticsForPeriod(query, startDate, endDate, webappPath);
        }
        return false;
    }

    private void generateGoogleSearchResults(JSONObject jsonObjectInput) {
        GoogleSearchAgent gs = (GoogleSearchAgent) AgentFactory.getAgent(AgentTypes.GOOGLE);
        gs.getListOfRequests().clear();
        String[] queryList = jsonObjectInput.get(Keys.Response.QUERY).toString().split(",");
        for (String query : queryList) {
            gs.getListOfRequests().add(query.trim());
        }
        gs.setvDuration(jsonObjectInput.get(Keys.Response.VDURATION).toString());
        gs.setqDuration(jsonObjectInput.get(Keys.Response.QDURATION).toString());
        gs.setLocalization(jsonObjectInput.get(Keys.Response.LOCALIZATION).toString());
        gs.setNumberOfPages(Integer.parseInt(jsonObjectInput.get(Keys.Response.NUM_OF_PAGES).toString()));
        gs.run();
    }
}
