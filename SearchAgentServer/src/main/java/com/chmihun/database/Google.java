package com.chmihun.database;

import com.chmihun.searchagent.GoogleObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Sergey.Chmihun on 05/16/2017.
 */
public class Google extends MySQLDB {
    private static final Logger logger = LoggerFactory.getLogger(Google.class.getName());
    private int lastID;
    private String dbTable = res.getString("dbTableGoogle");

    public int getLastID() {
        return lastID;
    }

    public Google() {
        setDbTable(dbTable);
        init();
        lastID = getIDOfLastEntry();
    }

    /** Just for tests */
    public Google(String dbName, String dbTable, String dbURL) {
        this.dbName = dbName;
        this.dbTable = dbTable;
        this.dbURL = dbURL;
        init();
    }

    @Override
    public void createTable() {
        Connection conn = createConnection(dbName);
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + dbTable + "(id integer not null, pTimestamp text not null, reqTitle text not null, googleLink text not null, sourceLink text not null)");
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
        Connection conn = createConnection(dbName);
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO " + dbTable + " VALUES (" + object.getID() + ", '" + object.getTimestamp() + "', '" + object.getreqTitle() + "', '" + object.getgLink() + "', '" + object.getsLink() + "');");
        } catch (SQLException e) {
            logger.error("Problems with inserting data into DB. ", e);
        } finally {
            closeStatement(stmt);
            closeConn(conn);
        }
    }
}
