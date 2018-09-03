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
    private String charset = "UTF-8";
    private String userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
    private static GoogleSearchAgent googleSearchAgent;

    private ArrayList<String> listOfRequests = new ArrayList<>();
    //private ArrayList<String> whiteList;
    private int numberOfPages;
    private int counterOfFoundRes;
    private String qDuration;
    private String vDuration;
    private String localization;
    private String attribute;
    private boolean isBannedByGoogle;
    //private WebDriver driver;
    //private WebDriverWait wait;

    /**
     * For tests
     */
    public ArrayList<String> getListOfRequests() {
        return listOfRequests;
    }

    public static GoogleSearchAgent getGoogleSearchAgent() {
        return googleSearchAgent;
    }

    public static void setGoogleSearchAgent(GoogleSearchAgent googleSearchAgent) {
        GoogleSearchAgent.googleSearchAgent = googleSearchAgent;
    }

    /**
     * Replace usual exception handler with own on creation of object of class
     */
    public GoogleSearchAgent() {
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

    /*public void setWhiteList(ArrayList<String> whiteList) {
        if (whiteList != null) {
            logger.debug("WhiteList size = " + whiteList.size());
        } else logger.debug("WhiteList is empty");
        this.whiteList = whiteList;
    }*/

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

            //closeBrowser();
            logger.info("Operation successfully finished! Found " + counterOfFoundRes + " links");
        }
    }

    /**
     * This method saves links to the DB, preliminarily checking them on the compliance with request and filtering allowed sources
     */
    public void saveLinks(String request) {
        logger.debug("Request = " + request);

        /** We go through all specified number of pages for current request */
        for (int i = 0; i < numberOfPages; i++) {
            Elements links = null;

            /** At this point we check whether ban is reached and choosing the way of working */
            if (!isBannedByGoogle) {
                String pages = "&start=" + i * 10;

                try {
                    links = Jsoup.connect(String.format("%s%s%s%s", localization, URLEncoder.encode(request, charset), attribute, pages)).userAgent(userAgent).get().select("a");
                    usualParser(links, request);
                    logger.debug("Number of all found links on the page #" + (i + 1) + " = " + links.size());
                } catch (IOException e) {
                    logger.error("Can't get webpage", e);
                    isBannedByGoogle = true;

                    /** Google ban handler */
                    /*if (e.toString().contains("Status=503")){
                        logger.debug("Google ban occurred. Initialized recovery mechanism");
                        *//*anInterface.setSuspended(true);
                        anInterface.getStatus().setText(res.getString("google.ban"));
                        anInterface.getStatus().setForeground(Color.RED);*//*

                        String urlToCaptcha = e.toString().substring(e.toString().lastIndexOf("URL") + 4);
                        bannedParser(urlToCaptcha, request);
                    }*/
                }
            } else {
                logger.error("Banned by Google");
                /*WebElement searchBar = driver.findElement(By.id("lst-ib"));
                if (!driver.getTitle().contains(request)) {
                    searchBar.clear();
                    searchBar.sendKeys(request);
                    searchBar.click();
                } else {
                    driver.findElement(By.xpath("//a[@id='pnnext']/span[2]")).click();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bannedParser(null, request);*/
            }
        }
    }

    /**
     * This method parses HTML page in usual situation by using jsoup connection
     */
    public void usualParser(Elements links, String request) {
        saveResults(links, request);
    }

    /** This method parses HTML page in situation when Google ban requests and asks to enter capture. In this case uses WebDriver. User need to enter capture only once per session */
    /*public void bannedParser (String urlToCaptcha, String request) {
        if (!urlToCaptcha.isEmpty()) {
            logger.debug("Error URL = " + urlToCaptcha);

            driver = new FirefoxDriver();
            driver.manage().window().setPosition(new Point(0, 0));
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(600, 500));
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            wait = new WebDriverWait(driver, 120);
            driver.navigate().to(urlToCaptcha);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("hdtbMenus")));
        }

        Elements links = null;

        *//** Check that search results are shown *//*
        if (driver.findElements(By.id("hdtbMenus")).size() != 0) {
            String link = driver.getCurrentUrl();
            try {
                links = Jsoup.connect(link).userAgent(userAgent).get().select("a");
            } catch (IOException e) {
                logger.error("Problems with getting links from page", e);
            }
        } else {
            *//*anInterface.getStatus().setText(res.getString("time.out"));
            anInterface.getStatus().setForeground(Color.RED);*//*
        }

        saveResults(links, request);
    }*/

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
        ArrayList<String> urls = new ArrayList<String>();

        String title = link.text();
        String alternativeRequest = "";
        if (request.toLowerCase().contains("ё")) {
            alternativeRequest = request.toLowerCase().replaceAll("ё", "е");
        }

        if (title.toLowerCase().contains(request.toLowerCase()) || (!alternativeRequest.isEmpty() && title.toLowerCase().contains(alternativeRequest))) {
            urls.add(link.absUrl("href")); // absUrl("href") - Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
            try {
                urls.add(URLDecoder.decode(urls.get(0).substring(urls.get(0).indexOf('=') + 1, urls.get(0).indexOf('&')), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error("Decoding issue", e);
                /*anInterface.setStopped(true);
                anInterface.getStatus().setText(res.getString("decoding.issue"));
                anInterface.getStatus().setForeground(Color.RED);*/
            }
            return urls;
        } else {
            return null;
        }
    }

    /** This method checks whether link contain names of the sites that are specified in the white list and skip them */
    /*public boolean checkPlayer(String url) {
        for (String item : whiteList) {
            if (url.contains(item)) return true;
        }

        *//** In this part program goes to every link and find all iframe elements and check if they equal to white list items. Don't work properly. I suppose there should be used multithreading. *//*
        *//*Elements iframes = null;
        try {
            iframes = Jsoup.connect(url).userAgent(userAgent).get().select("iframe");
        } catch (IOException e) {
            anInterface.getStatus().setText(res.getString("connection.issue"));
            anInterface.getStatus().setForeground(Color.RED);
        }

        for (Element element : iframes) {
            if (element != null && element.attr("src").contains("http")) {
                String videoLink = element.attr("src");

                for (String item : whiteList) {
                    if (videoLink.contains(item)) return true;
                }
            }
        }*//*

        return false;
    }*/

    /** This method closes browser, if he has been launched */
    /*public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }*/
}