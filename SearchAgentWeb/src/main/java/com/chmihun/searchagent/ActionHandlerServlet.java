package com.chmihun.searchagent;

import com.chmihun.searchagent.agents.GoogleSearch;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * Created by Sergey.Chmihun on 07/03/2017.
 */
@WebServlet(name = "ActionHandlerServlet", urlPatterns = {"/action"})
public class ActionHandlerServlet extends javax.servlet.http.HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ActionHandlerServlet.class.getName());
    private GoogleSearch gs;

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObjectInput = null;

        try {
            jsonObjectInput = (JSONObject) parser.parse(request.getReader());
        } catch (ParseException e) {
            logger.error("Problems with parsing json input. ", e);
        }

        try {
            if (jsonObjectInput != null) {
                logger.debug("Incoming json: " + jsonObjectInput.toJSONString());

                if (jsonObjectInput.get("action").equals("getGoogleSearchResults")) {
                    gs = GoogleSearch.getGoogleSearchServer();
                    gs.getListOfRequests().clear();
                    gs.getListOfRequests().add(jsonObjectInput.get("query").toString());
                    gs.setvDuration(jsonObjectInput.get("vDuration").toString());
                    gs.setqDuration(jsonObjectInput.get("qDuration").toString());
                    gs.setLocalization(jsonObjectInput.get("localization").toString());
                    gs.setNumberOfPages(Integer.parseInt(jsonObjectInput.get("numOfPages").toString()));
                    gs.run();

                    JSONObject jsonObjectOutput = new JSONObject();
                    jsonObjectOutput.put("result", 1);

                    response.setContentType("application/json");
                    response.getWriter().write(jsonObjectOutput.toString());
                    logger.debug("Outgoing json: " + jsonObjectOutput.toString());
                } else if (jsonObjectInput.get("action").equals("getStatistics")) {
                    gs = GoogleSearch.getGoogleSearchServer();
                    boolean isAllRight = gs.generateStatisticsForPeriod(jsonObjectInput.get("query").toString(), jsonObjectInput.get("startDate").toString(), jsonObjectInput.get("endDate").toString());

                    JSONObject jsonObjectOutput = new JSONObject();
                    if (isAllRight) {
                        jsonObjectOutput.put("result", 1);
                    } else {
                        jsonObjectOutput.put("result", 0);
                    }

                    response.setContentType("application/json");
                    response.getWriter().write(jsonObjectOutput.toString());
                    logger.debug("Outgoing json: " + jsonObjectOutput.toString());
                }
            }
        } catch (Exception e) {
            JSONObject jsonObjectOutput = new JSONObject();
            jsonObjectOutput.put("result", 0);
            response.setContentType("application/json");
            response.getWriter().write(jsonObjectOutput.toString());
            logger.error("Outgoing json: " + jsonObjectOutput.toString() + ". Something went wrong: " + e);
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }
}
