package com.chmihun.searchagent;

public class Keys {

    public class Response {
        public static final String ACTION = "action";
        public static final String RESULT = "result";

        // Action names
        public static final String GET_GOOGLE_SEARCH_RESULTS = "getGoogleSearchResults";
        public static final String GET_STATISTICS = "getStatistics";

        // Google Search parameters
        public static final String QUERY = "query";
        public static final String VDURATION = "vDuration";
        public static final String QDURATION = "qDuration";
        public static final String LOCALIZATION = "localization";
        public static final String NUM_OF_PAGES = "numOfPages";

        // Statistics parameters
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
    }

    public class Export {
        public static final String FOLDER_NAME = "folderName";
        public static final String FILE_OUTPUT_NAME = "fileOutputName";
        public static final String COLUMN_NAME = "columnName";
    }
}
