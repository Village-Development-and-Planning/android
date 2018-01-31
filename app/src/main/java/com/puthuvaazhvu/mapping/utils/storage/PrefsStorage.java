package com.puthuvaazhvu.mapping.utils.storage;

import android.content.SharedPreferences;

/**
 * Created by muthuveerappans on 10/30/17.
 */


@Deprecated
public class PrefsStorage {
    public static PrefsStorage prefsStorage;

    private final SharedPreferences sharedPreferences;

    public static PrefsStorage getInstance(SharedPreferences sharedPreferences) {
        if (prefsStorage == null) {
            prefsStorage = new PrefsStorage(sharedPreferences);
        }
        return prefsStorage;
    }

    private PrefsStorage(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void saveLatestSurveyID(String surveyID) {
        saveString("latest_survey_id", surveyID);
    }

    public String getLatestSurveyID() {
        return getString("latest_survey_id");
    }

    private String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    private void saveString(String key, String content) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, content);
        editor.apply();
    }
}
