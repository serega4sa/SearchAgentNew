package com.chmihun.searchagent.databases;

public enum DBType {

    GOOGLE("google"),
    GOOGLEBACKUP("googleBackup");

    private String dbTypeName;

    DBType(String dbTypeName) {
        this.dbTypeName = dbTypeName;
    }

    public static DBType getDBTypeByName(String dbTypeName) {
        for (DBType dbType : DBType.values()) {
            if (dbType.dbTypeName.equals(dbTypeName)) {
                return dbType;
            }
        }
        return null;
    }
}
