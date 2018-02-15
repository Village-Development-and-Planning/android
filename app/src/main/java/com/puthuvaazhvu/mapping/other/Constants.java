package com.puthuvaazhvu.mapping.other;

/**
 * Created by muthuveerappans on 9/28/17.
 */

public class Constants {
    public static String LOG_TAG = "mapping.logs";
    public static Language APP_LANGUAGE = Language.TAMIL;
    public static String DATA_DIR = "com.puthuvaazhvu.mapping.data";
    public static String PREFS = "com.puthuvaazhvu.mapping.prefs";
    public static String SURVEY_DATA_DIR = "surveys";
    public static String ANSWERS_DATA_DIR = "logged-answers";
    public static String COMPLETED_ANSWERS_DATA_DIR = "answers";
    public static String INFO_FILE_NAME = "info.json";
    public static String AUTH_FILE_NAME = "auth.json";

    public static class PermissionRequestCodes {
        public static final int REQUEST_GPS_CODE = 1;
        public static final int STORAGE_PERMISSION_REQUEST_CODE = 2;
    }

    public enum Language {
        ENGLISH,
        TAMIL
    }
}
