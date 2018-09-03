package com.chmihun.searchagent.agents;

import com.chmihun.searchagent.Keys;
import com.chmihun.searchagent.databases.DBFactory;
import com.chmihun.searchagent.databases.DBType;
import com.chmihun.searchagent.databases.GoogleDB;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class ExportController {

    private static final Logger logger = LoggerFactory.getLogger(ExportController.class.getName());
    private static ResourceBundle res = ResourceBundle.getBundle("common");

    /**
     * This method combines results of queried title for the specified time period to .xml file
     * @return true, if needed data is present and file is successfully generated, otherwise false
     **/
    public static boolean generateStatisticsForPeriod(String query, String startDate, String endDate, String webappPath) {
        ArrayList<String> stat = ((GoogleDB) DBFactory.getDatabaseInstance(DBType.GOOGLE)).getStatisticsForPeriod(query, startDate, endDate);

        try {
            if (stat != null) {
                Date currentDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
                new File(String.format(res.getString(Keys.Export.FOLDER_NAME), webappPath)).mkdir();
                String fileOutputNameXls = String.format(res.getString(Keys.Export.FILE_OUTPUT_NAME), webappPath, format.format(currentDate));
                File excelFile = new File(fileOutputNameXls);
                WritableWorkbook workbook = Workbook.createWorkbook(excelFile);
                WritableSheet sheet = workbook.createSheet(query, 0);

                Label cell = null;
                for (int i = 0; i < stat.size(); i++) {
                    String[] arr = stat.get(i).split(", ");
                    for (int j = 1; j < 6; j++) {
                        if (i == 0) {
                            WritableCellFormat cellFormat = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true));
                            cell = new Label(j, i + 1, res.getString(String.format(res.getString(Keys.Export.COLUMN_NAME), j)), cellFormat);
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
