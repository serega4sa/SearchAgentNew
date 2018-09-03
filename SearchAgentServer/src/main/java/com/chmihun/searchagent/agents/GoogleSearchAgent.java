package com.chmihun.searchagent.agents;

import com.chmihun.searchagent.databases.DBFactory;
import com.chmihun.searchagent.databases.DBType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Sergey.Chmihun on 06/27/2017.
 */
public class GoogleSearchAgent extends Agent implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSearchAgent.class.getName());
    private static final String CHARSET = "UTF-8";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
    private static GoogleSearchAgent googleSearchAgent = new GoogleSearchAgent();

    private ArrayList<String> listOfRequests = new ArrayList<>();
    private int numberOfPages;
    private int counterOfFoundRes;
    private String qDuration;
    private String vDuration;
    private String localization;
    private String attribute;
    private boolean isBannedByGoogle;

    /**
     * Replace usual exception handler with own on creation of object of class
     */
    private GoogleSearchAgent() {
        Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
        googleSearchAgent = this;
        DBFactory.createDBInstance(DBType.GOOGLE);
        logger.debug("Google search agent initialized [" + googleSearchAgent + "]");
    }

    /**
     * Method that writes all uncaught exceptions to log file
     */
    public void handleUncaughtException(Thread t, Throwable ex) {
        logger.error("Uncaught exception in thread: " + t.getName(), ex);
    }

    public static GoogleSearchAgent getGoogleSearchAgentInstance() {
        return googleSearchAgent;
    }

    /**
     * For tests
     */
    public ArrayList<String> getListOfRequests() {
        return listOfRequests;
    }

    public void addRequestTitle(String title) {
        listOfRequests.add(title);
    }

    public void clearRequestsList() {
        listOfRequests.clear();
    }

    public void setvDuration(String vDuration) {
        logger.debug("vDuration = " + vDuration);
        if (vDuration.equals("any")) this.vDuration = "";
        if (vDuration.equals("medium")) this.vDuration = "m";
        if (vDuration.equals("long")) this.vDuration = "l";
    }

    public void setqDuration(String qDuration) {
        logger.debug("qDuration = " + qDuration);
        if (qDuration.equals("any")) this.qDuration = "";
        if (qDuration.equals("hour")) this.qDuration = "h";
        if (qDuration.equals("day")) this.qDuration = "d";
        if (qDuration.equals("week")) this.qDuration = "w";
        if (qDuration.equals("month")) this.qDuration = "m";
        if (qDuration.equals("year")) this.qDuration = "y";
    }

    public void setLocalization(String location) {
        logger.debug("GoogleLocation = " + location);
        if (location.equals("ua")) this.localization = "http://www.google.com.ua/search?q=";
        if (location.equals("ru")) this.localization = "http://www.google.ru/search?q=";
    }

    public void setNumberOfPages(int numberOfPages) {
        logger.debug("NumberOfPages=" + numberOfPages);
        this.numberOfPages = numberOfPages;
    }

    /**
     * This method depending on the info that user specified creates attribute
     */
    public void createAttribute() {
        if (!qDuration.isEmpty()) {
            if (!vDuration.isEmpty()) {
                attribute = "&tbs=dur:" + vDuration + ",qdr:" + qDuration + "&tbm=vid";
            } else {
                attribute = "&tbs=qdr:" + qDuration + "&tbm=vid";
            }
        } else {
            if (!vDuration.isEmpty()) {
                attribute = "&tbs=dur:" + vDuration + "&tbm=vid";
            } else {
                attribute = "&tbm=vid";
            }
        }
    }

    public void run() {
        logger.debug("RequestsList size = " + listOfRequests.size());
        if (!listOfRequests.isEmpty()) {
            createAttribute();

            for (String item : listOfRequests) {
                saveLinks(item);
            }

            logger.info("Operation successfully finished! Found " + counterOfFoundRes + " links");
        }
    }

    /**
     * This method saves links to the DB, preliminarily checking them on the compliance with request and filtering allowed sources
     */
    public void saveLinks(String request) {
        logger.debug("Request = " + request);

        // We go through all specified number of pages for current request
        for (int i = 0; i < numberOfPages; i++) {
            Elements links = null;

            // At this point we check whether ban is reached and choosing the way of working
            if (!isBannedByGoogle) {
                String pages = "&start=" + i * 10;

                try {
                    links = Jsoup.connect(String.format("%s%s%s%s", localization, URLEncoder.encode(request, CHARSET), attribute, pages)).userAgent(USER_AGENT).get().select("a");
                    saveResults(links, request);
                    logger.debug("Number of all found links on the page #" + (i + 1) + " = " + links.size());
                } catch (IOException e) {
                    logger.error("Can't get webpage. ", e);
                    isBannedByGoogle = true;
                }
            } else {
                logger.error("Banned by Google");
                break;
            }
        }
    }

    /**
     * This method inserts data to DB, if request is matching
     */
    public void saveResults(Elements links, String request) {
        for (Element link : links) {
            ArrayList<String> urls = checkMatchingRequest(link, request);

            if (urls != null) {
                GoogleObj gObj = new GoogleObj(request, urls.get(0), urls.get(1), DBFactory.getDatabaseInstance(DBType.GOOGLE));
                DBFactory.getDatabaseInstance(DBType.GOOGLE).insertDataToDB(gObj);
                DBFactory.getDatabaseInstance(DBType.GOOGLEBACKUP).insertDataToDB(gObj);
                counterOfFoundRes++;
            }
        }
    }

    /**
     * This method returns element that matches to the request
     */
    public ArrayList<String> checkMatchingRequest(Element link, String request) {
        ArrayList<String> urls = new ArrayList<>();

        String title = link.text();
        String alternativeRequest = "";
        if (request.toLowerCase().contains("ё")) {
            alternativeRequest = request.toLowerCase().replaceAll("ё", "е");
        }

        if (title.toLowerCase().contains(request.toLowerCase()) || (!alternativeRequest.isEmpty() && title.toLowerCase().contains(alternativeRequest))) {
            // absUrl("href") - Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>"
            urls.add(link.absUrl("href"));
            try {
                urls.add(URLDecoder.decode(urls.get(0).substring(urls.get(0).indexOf('=') + 1, urls.get(0).indexOf('&')), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error("Decoding issue", e);
            }
            return urls;
        } else {
            return null;
        }
    }
}
