package com.puthuvaazhvu.mapping.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.puthuvaazhvu.mapping.modals.surveyorinfo.SurveyorInfo;
import com.puthuvaazhvu.mapping.other.Constants;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class SharedPreferenceUtils {
    private static SharedPreferenceUtils sharedPreferenceUtils;

    private final Gson gson;
    private final SharedPreferences sharedPreferences;

    public static SharedPreferenceUtils getInstance(Context context) {
        if (sharedPreferenceUtils == null) {
            sharedPreferenceUtils = new SharedPreferenceUtils(context);
        }
        return sharedPreferenceUtils;
    }

    private SharedPreferenceUtils(Context context) {
        this.sharedPreferences = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public SurveyorInfo getSurveyorInfo() {
        String json = sharedPreferences.getString("surveyor_info", null);
        if (json != null) {
            return gson.fromJson(json, SurveyorInfo.class);
        }
        return null;
    }

    public void putSurveyorInfo(SurveyorInfo surveyorInfo) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("surveyor_info", surveyorInfo.toJson().toString());
        editor.apply();
    }
}
