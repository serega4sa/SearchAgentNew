package com.chmihun.searchagent.databases;

import com.chmihun.searchagent.agents.GoogleObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Sergey.Chmihun on 05/16/2017.
 */
public class GoogleDB extends MySQLDB {

    private static final Logger logger = LoggerFactory.getLogger(GoogleDB.class.getName());
    private static final String DB_TABLE_NAME = "dbTableGoogle";

    public GoogleDB() {
        setDbTable(res.getString(DB_TABLE_NAME));
        setLastID(getIDOfLastEntry());
        init();
        logger.debug("GoogleDB was created and initialized.");
    }

    /**
     * Just for tests
     */
    public GoogleDB(String dbName, String dbTable, String dbURL) {
        this.dbName = dbName;
        setDbTable(dbTable);
        this.dbURL = dbURL;
        init();
    }

    @Override
    public void createTable() {
        createTable("CREATE TABLE IF NOT EXISTS " + getDbTable() + "(id integer not null, pTimestamp text not null, reqTitle text not null, sourceSite text not null, googleLink text not null, sourceLink text not null)");
    }

    void createTable(String sql) {
        Connection conn = createConnection(dbName);
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error("Problems with creating table. ", e);
        } finally {
            closeStatement(stmt);
            closeConn(conn);
        }
    }

    @Override
    public void insertDataToDB(Object obj) {
        GoogleObj googleObj = (GoogleObj) obj;

        if (!isPresentInDB(googleObj.getsLink())) {
            insertDataToDB(googleObj);
        }
    }

    void insertDataToDB(GoogleObj googleObj) {
        // Cut direct domain name of pirate site from sLink
        String temp = googleObj.getsLink().substring(googleObj.getsLink().indexOf("//") + 2);
        String sourceSite = temp.substring(0, temp.indexOf("/"));
        if (sourceSite.contains("www.")) {
            sourceSite = sourceSite.substring(4);
        }

        Connection conn = createConnection(dbName);
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO " + getDbTable() + " VALUES (" + googleObj.getID() + ", '" + googleObj.getTimestamp() + "', '" + googleObj.getreqTitle() + "', '" + sourceSite + "', '" + googleObj.getgLink() + "', '" + googleObj.getsLink() + "');");
        } catch (SQLException e) {
            logger.error("Problems with inserting data into DB. ", e);
        } finally {
            closeStatement(stmt);
            closeConn(conn);
        }
    }

    /**
     * Checks whether passed link is present in database
     * @return true, if link is already present in database
     */
    public boolean isPresentInDB(String sLink) {
        Connection conn = createConnection(dbName);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM " + getDbTable() + " WHERE sourceLink = ?");
            pstmt.setString(1, sLink);
            rs = pstmt.executeQuery();
            rs.next();

            return rs.getInt(1) == 1;
        } catch (SQLException e) {
            logger.error("Problems with query of getting value from DB. ", e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(pstmt);
            closeConn(conn);
        }
        return false;
    }

    /**
     * Creates statistics list for specified title
     * @return list of results for the requested title and time period
     */
    @Override
    public ArrayList<String> getStatisticsForPeriod(String query, String startDate, String endDate) {
        ArrayList<String> statList = new ArrayList<String>();

        Connection conn = createConnection(dbName);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM " + getDbTable() + " WHERE reqTitle=? AND pTimestamp BETWEEN ? AND ?");
            pstmt.setString(1, query);
            pstmt.setString(2, startDate);
            pstmt.setString(3, endDate);
            rs = pstmt.executeQuery();

            int columnCount = rs.getMetaData().getColumnCount();

            StringBuilder strBld = new StringBuilder();
            while (rs.next()) {
                strBld.setLength(0);
                for (int i = 2; i <= columnCount; i++) {
                    strBld.append(rs.getObject(i)).append(", ");
                }
                statList.add(strBld.toString());
            }
        } catch (SQLException e) {
            logger.error("Problems with creating table. ", e);
        } finally {
            closeStatement(pstmt);
            closeConn(conn);
        }
        return statList;
    }
}
