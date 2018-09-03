package com.chmihun.searchagent.databases;

import java.util.HashMap;
import java.util.Map;

public class DBFactory {

    private static Map<DBType, MySQLDB> databases = new HashMap<>();

    public static void createDBInstance(DBType dbType) {
        if (!databases.containsKey(dbType)) {
            switch (dbType) {
                case GOOGLE:
                    databases.put(dbType, new GoogleDB());
                    databases.put(dbType, new GoogleDBBackup());
                    break;
            }
        }
    }

    public static MySQLDB getDatabaseInstance(DBType dbType) {
        return databases.getOrDefault(dbType, null);
    }
}
