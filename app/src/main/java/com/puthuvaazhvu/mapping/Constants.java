package com.puthuvaazhvu.mapping;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Constants {
    public static String LOG_TAG = "puthuvaazhvu_logs";
    public static boolean DEBUG = true;
    public static boolean isTamil = false;

    public static class IntentKeys {
        public static final String SurveyActivity_survey_data_string = "SurveyActivity_survey_data_string";
    }

    public static class ErrorCodes {
        public static int NULL_DATA = -1;
    }

    public static class ErrorMessages {
        public static int NULL_DATA = R.string.no_data;
    }
}
