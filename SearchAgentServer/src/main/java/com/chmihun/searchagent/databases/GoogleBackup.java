package com.chmihun.searchagent.databases;

import com.chmihun.searchagent.agents.GoogleObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Sergey.Chmihun on 07/06/2017.
 */
public class GoogleBackup extends MySQLDB {
    private static final Logger logger = LoggerFactory.getLogger(GoogleBackup.class.getName());

    public GoogleBackup() {
        setDbTable(res.getString("dbTableGoogleBackup"));
        setLastID(getIDOfLastEntry());
        init();
    }

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
            stmt.executeUpdate("INSERT INTO " + getDbTable() + " VALUES (" + object.getID() + ", '" + object.getTimestamp() + "', '" + object.getreqTitle() + "', '" + sourceSite + "', '" + object.getgLink() + "', '" + object.getsLink() + "');");
        } catch (SQLException e) {
            logger.error("Problems with inserting data into DB. ", e);
        } finally {
            closeStatement(stmt);
            closeConn(conn);
        }
    }
}
