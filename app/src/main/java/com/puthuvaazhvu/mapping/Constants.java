package com.puthuvaazhvu.mapping;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Constants {
    public static String LOG_TAG = "puthuvaazhvu_logs";
    public static boolean DEBUG = true;
    public static boolean isTamil = false;
    public static String APP_PACKAGE = "com.puthuvaazhvu.mapping";

    public static String SPLIT_FOR_N_TAG = "<n_id>";

    public static class DataStorage {
        public static final String APP_DIR_SURVEY = APP_PACKAGE + "_surveydata";
    }

    public static class IntentKeys {
        public static final String SurveyActivity_survey_data_string = "SurveyActivity_survey_data_string";
    }

    public static class ErrorCodes {
        public static int NULL_DATA = -1;
        public static int PARSING_ERROR = -2;
        public static int SAVING_ERROR = -3;
        public static int FILE_STORAGE_NULL = -4;
    }

    public static class ErrorMessages {
        public static int NULL_DATA = R.string.no_data;
    }
}
