package com.puthuvaazhvu.mapping.other;

/**
 * Created by muthuveerappans on 9/28/17.
 */

public class Constants {
    public static String LOG_TAG = "mapping.logs";
    public static Language APP_LANGUAGE = Language.ENGLISH;
    public static String DATA_DIR = "com.puthuvaazhvu.mapping.data";

    public static class PermissionRequestCodes {
        public static final int REQUEST_GPS_CODE = 1;
        public static final int STORAGE_PERMISSION_REQUEST_CODE = 2;
    }

    public static enum Language {
        ENGLISH,
        TAMIL
    }
}
