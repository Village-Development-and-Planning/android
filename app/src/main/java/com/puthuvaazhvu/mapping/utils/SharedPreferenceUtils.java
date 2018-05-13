package com.puthuvaazhvu.mapping.utils;

import android.content.SharedPreferences;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class SharedPreferenceUtils {

    public static String getSurveyorID(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("last_session_survey_id", null);
    }

    public static void putSurveyID(SharedPreferences sharedPreferences, String surveyID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_session_survey_id", surveyID);
        editor.apply();
    }
}
