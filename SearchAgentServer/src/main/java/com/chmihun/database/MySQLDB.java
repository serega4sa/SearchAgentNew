package com.chmihun.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ResourceBundle;

/**
 * Created by Sergey.Chmihun on 05/03/2017.
 */
public abstract class MySQLDB {
    private static final Logger logger = LoggerFactory.getLogger(MySQLDB.class.getName());
    static ResourceBundle res = ResourceBundle.getBundle("common");
    String dbURL = res.getString("dbURL");
    String dbName = res.getString("dbName");
    private String dbTable;
    private String dbUser = res.getString("dbUser");
    private String dbPass = res.getString("dbPass");

    public void setDbTable(String dbTable) {
        this.dbTable = dbTable;
    }

    /** Init database: check for existence of DB and exact table inside it */
    public void init() {
        if (isDBReady()) {
            logger.debug("DB is ready to use");
        } else {
            logger.debug("DB isn't ready to use");
        }
    }

    /** Checking that database and table are present, otherwise create them */
    public boolean isDBReady() {
        int count = 0;

        if (isDBExist()) count++;
        /* Creating table, if it already exist, table won't be created (implemented to SQL)*/
        createTable();

        return count == 1;
    }

    /** Check that database exists */
    public boolean isDBExist() {
        Connection conn = createConnection("default");
        Statement stmt = null;
        ResultSet rsDB = null;
        try {
            stmt = conn.createStatement();
            rsDB = stmt.executeQuery("SELECT COUNT(*) SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + dbName + "'");
            rsDB.next();
            if (rsDB.getInt(1) == 0) {
                createDB();
                isDBExist();
                return true;
            } else return true;
        } catch (SQLException e) {
            logger.error("Problems with query of finding " + dbName + " database in list of existing DB. ", e);
        } finally {
            closeStatement(stmt);
            closeResultSet(rsDB);
            closeConn(conn);
        }
        return false;
    }

    /** Get ID of the last entry in DB */
    public int getIDOfLastEntry() {
        Connection conn = createConnection(dbName);
        Statement stmt = null;
        ResultSet rsCount = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rsCount = stmt.executeQuery("SELECT COUNT(id) AS result FROM " + dbTable);
            rsCount.next();
            if (rsCount.getInt("result") != 0) {
                rs = stmt.executeQuery("SELECT id AS idValue FROM " + dbTable + " ORDER BY id DESC LIMIT 1");
                rs.next();
                return rs.getInt("idValue");
            }
        } catch (SQLException e) {
            logger.error("Problems with query of getting ID of last entry in DB. ", e);
        } finally {
            closeStatement(stmt);
            closeResultSet(rsCount);
            closeResultSet(rs);
            closeConn(conn);
        }

        return 0;
    }

    public Connection createConnection(String dbName) {
        String param = (dbName == null) ? "" : dbName;
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (dbName != null && dbName.equals("default")) {
                connection = DriverManager
                        .getConnection(dbURL + "?user=" + dbUser + "&password=" + dbPass);
            } else {
                connection = DriverManager
                        .getConnection(dbURL + param,
                                dbUser, dbPass);
            }
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            closeConn(connection);
            logger.error("Problems with connection to DB. ", e);
        } catch (ClassNotFoundException e) {
            logger.error("Driver for MySQL isn't found. ", e);
        }
        return connection;
    }

    public void createDB() {
        Connection conn = createConnection("default");
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE " + dbName);
            createTable();
        } catch (SQLException e) {
            logger.error("Problems with creating DB. ", e);
        } finally {
            closeStatement(stmt);
            closeConn(conn);
        }
    }

    public void removeDB() {
        Connection conn = createConnection(dbName);
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("DROP DATABASE IF EXISTS " + dbName);
        } catch (SQLException e) {
            logger.error("Problems with deleting DB. ", e);
        } finally {
            closeStatement(stmt);
            closeConn(conn);
        }
    }

    public void closePreparedStatement(PreparedStatement pstmt) {
        try {
            if (pstmt != null)
                pstmt.close();
        } catch (SQLException e) {
            logger.error("Problems with closing prepared statement. ", e);
        }
    }

    public void closeStatement(Statement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException e) {
            logger.error("Problems with closing statement. ", e);
        }
    }

    public void closeResultSet(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            logger.error("Problems with closing result set. ", e);
        }
    }

    public void closeConn(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            logger.error("Problems with closing connection. ", e);
        }
    }

/** ----- ABSTRACT METHODS ------------------------------------------------------------------------------- */

    public abstract void createTable();

    public abstract void insertDataToDB(Object object);

/** --------------------------------------------------------------------------------------------------- */

    /** Returns true, if link is already in DB */
    public boolean isPresentInDB(String sLink) {
        Connection conn = createConnection(dbName);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM " + dbTable + " WHERE sLink = ?");
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
