package com.chmihun.searchagent.databases;

import com.chmihun.searchagent.agents.GoogleObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Created by Sergey.Chmihun on 05/16/2017.
 */
public class Google extends MySQLDB {
    private static final Logger logger = LoggerFactory.getLogger(Google.class.getName());

    public Google() {
        setDbTable(res.getString("dbTableGoogle"));
        setLastID(getIDOfLastEntry());
        init();
    }

    /** Just for tests */
    public Google(String dbName, String dbTable, String dbURL) {
        this.dbName = dbName;
        setDbTable(dbTable);
        this.dbURL = dbURL;
        init();
    }

    @Override
    public void createTable() {
        Connection conn = createConnection(dbName);
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + getDbTable() + "(id integer not null, pTimestamp text not null, reqTitle text not null, sourceSite text not null, googleLink text not null, sourceLink text not null)");
        } catch (SQLException e) {
            logger.error("Problems with creating table. ", e);
        } finally {
            closeStatement(stmt);
            closeConn(conn);
        }
    }

    @Override
    public void insertDataToDB(Object obj) {
        GoogleObj object = (GoogleObj) obj;

        /** Cut direct domain name of pirate site from sLink */
        String temp = object.getsLink().substring(object.getsLink().indexOf("//") + 2);
        String sourceSite = temp.substring(0, temp.indexOf("/"));
        if (sourceSite.contains("www.")) {
            sourceSite = sourceSite.substring(4);
        }

        Connection conn = createConnection(dbName);
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            if (!isPresentInDB(object.getsLink())) {
                stmt.executeUpdate("INSERT INTO " + getDbTable() + " VALUES (" + object.getID() + ", '" + object.getTimestamp() + "', '" + object.getreqTitle() + "', '" + sourceSite + "', '" + object.getgLink() + "', '" + object.getsLink() + "');");
            }
        } catch (SQLException e) {
            logger.error("Problems with inserting data into DB. ", e);
        } finally {
            closeStatement(stmt);
            closeConn(conn);
        }
    }

    /** Returns true, if link is already in DB */
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
}
