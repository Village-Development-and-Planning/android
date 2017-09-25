package com.puthuvaazhvu.mapping;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Constants {
    public static String LOG_TAG = "puthuvaazhvu_logs";
    public static boolean DEBUG = true;
    public static boolean isTamil = false;
    public static String APP_PACKAGE = "com.puthuvaazhvu.mapping";

    public static class APIDataConstants {
        public static final String MULTIPLE_ITERATION = "MULTIPLE_ITERATION";
        public static final String SINGLE_ITERATION = "SINGLE_ITERATION";
        public static final String LOOP = "LOOP";
        public static final String BINARY = "BINARY";
        public static final String SINGLE_CHOICE = "SINGLE_CHOICE";
        public static final String MULTIPLE_CHOICE = "MULTIPLE_CHOICE";
        public static final String INPUT = "INPUT";
        public static final String TAG_GPS = "GPS";
        public static final String TAG_SURVEYOR_CODE = "SURVEYOR_CODE";
        public static final String TAG_SURVEYOR_DISTRICT = "SURVEYOR_DISTRICT";
        public static final String TAG_SURVEYOR_BLOCK = "SURVEYOR_BLOCK";
        public static final String TAG_SURVEYOR_PANCHAYAT = "SURVEYOR_PANCHAYAT";
        public static final String TAG_SURVEYOR_VILLAGE = "SURVEYOR_VILLAGE";
        public static final String TAG_SURVEYOR_HABITATIONS = "SURVEYOR_HABITATIONS";
    }

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
        public static int ITERATION_ERROR = R.string.question_iteration_error;
        public static int SURVEY_INCOMPLETE = R.string.survey_incomplete;
        public static int OPTIONS_NOT_SELECTED = R.string.options_not_selected;
    }
}
