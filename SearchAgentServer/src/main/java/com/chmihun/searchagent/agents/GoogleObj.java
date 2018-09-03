package com.chmihun.searchagent.agents;

import com.chmihun.searchagent.databases.GoogleDB;
import com.chmihun.searchagent.databases.MySQLDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Sergey.Chmihun on 04/28/2017.
 */
public class GoogleObj {
    public static int ID;
    private String reqTitle;
    private String timestamp;
    private String gLink;
    private String sLink;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GoogleObj(String reqTitle, String gLink, String sLink, MySQLDB database) {
        if (database instanceof GoogleDB) {
            GoogleDB db = (GoogleDB) database;
            if (!db.isPresentInDB(sLink)) {
                if (ID < db.getLastID())
                    ID = db.getLastID();
                this.ID++;
            }
        }

        this.reqTitle = reqTitle;
        this.timestamp = format.format(Calendar.getInstance().getTime());
        this.gLink = gLink;
        this.sLink = sLink;
    }

    @Override
    public String toString() {
        return "GoogleObj{" +
                "id=" + ID +
                ", timestamp='" + timestamp + '\'' +
                ", gLink='" + gLink + '\'' +
                ", sLink='" + sLink + '\'' +
                '}';
    }

    public int getID() {
        return ID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getgLink() {
        return gLink;
    }

    public String getsLink() {
        return sLink;
    }

    public String getreqTitle() {
        return reqTitle;
    }
}
