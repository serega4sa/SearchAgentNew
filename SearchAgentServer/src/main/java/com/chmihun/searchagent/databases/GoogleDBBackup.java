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
public class GoogleDBBackup extends GoogleDB {

    private static final Logger logger = LoggerFactory.getLogger(GoogleDBBackup.class.getName());
    private static final String DB_TABLE_NAME = "dbTableGoogleBackup";

    public GoogleDBBackup() {
        setDbTable(res.getString(DB_TABLE_NAME));
        init();
        logger.debug("GoogleDBBackup was created and initialized.");
    }

    @Override
    public void createTable() {
        createTable("CREATE TABLE IF NOT EXISTS " + getDbTable() + "(pTimestamp text not null, reqTitle text not null, sourceSite text not null, googleLink text not null, sourceLink text not null)");
    }

    @Override
    public void insertDataToDB(Object obj) {
        insertDataToDB(((GoogleObj) obj));
    }
}
