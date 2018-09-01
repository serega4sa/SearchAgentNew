package com.chmihun.searchagent.agents;

import com.chmihun.searchagent.databases.Google;
import com.chmihun.searchagent.databases.GoogleBackup;
import com.chmihun.searchagent.databases.MySQLDB;
import jxl.Workbook;
import jxl.write.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Sergey.Chmihun on 06/27/2017.
 */
public class GoogleSearch extends Agent implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSearch.class.getName());
    private String charset = "UTF-8";
    private String userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
    private static ResourceBundle res = ResourceBundle.getBundle("common");
    private static GoogleSearch googleSearchServer;

    private ArrayList<MySQLDB> databeses = new ArrayList<>();
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
    private String fileOutputNameXls;

    /**
     * For tests
     */
    public ArrayList<String> getListOfRequests() {
        return listOfRequests;
    }

    public static GoogleSearch getGoogleSearchServer() {
        return googleSearchServer;
    }

    public static void setGoogleSearchServer(GoogleSearch googleSearchServer) {
        GoogleSearch.googleSearchServer = googleSearchServer;
    }

    public String getFileOutputNameXls() {
        return fileOutputNameXls;
    }

    /**
     * Replace usual exception handler with own on creation of object of class
     */
    public GoogleSearch() {
        Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
        googleSearchServer = this;
        logger.debug("Google search agent initialized [" + googleSearchServer + "]");
        databeses.add(new Google());
        databeses.add(new GoogleBackup());
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
                GoogleObj gObj = new GoogleObj(request, urls.get(0), urls.get(1), databeses.get(0));
                databeses.get(0).insertDataToDB(gObj);
                databeses.get(1).insertDataToDB(gObj);
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

    /**
     * This method returns results for the requesting time period
     **/
    public boolean generateStatisticsForPeriod(String query, String startDate, String endDate, String webappPath) {
        ArrayList<String> stat = ((Google) databeses.get(0)).getStatisticsForPeriod(query, startDate, endDate);

        try {
            if (stat.size() > 0) {
                Date currentDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
                new File(webappPath + "\\statistics").mkdir();
                fileOutputNameXls = webappPath + "\\statistics\\Results_" + format.format(currentDate) + ".xls";
                File excelFile = new File(fileOutputNameXls);
                WritableWorkbook workbook = Workbook.createWorkbook(excelFile);
                WritableSheet sheet = workbook.createSheet(query, 0);

                Label cell = null;
                for (int i = 0; i < stat.size(); i++) {
                    String[] arr = stat.get(i).split(", ");
                    for (int j = 1; j < 6; j++) {
                        if (i == 0) {
                            WritableCellFormat cellFormat = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true));
                            cell = new Label(j, i + 1, res.getString("title.col" + j), cellFormat);
                            sheet.addCell(cell);
                        }
                        cell = new Label(j, i + 2, arr[j - 1]);
                        sheet.addCell(cell);
                    }
                }

                workbook.write();
                workbook.close();
                return true;
            } else throw new NullPointerException("Array of results is empty. ");
        } catch (Exception e) {
            logger.error("Something went wrong. ", e);
            return false;
        }
    }
}
