package org.ptracking.vdp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import org.ptracking.vdp.modals.surveyorinfo.SurveyorInfoFromAPI;
import org.ptracking.vdp.other.Constants;

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

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SurveyorInfoFromAPI getSurveyorInfo() {
        String json = sharedPreferences.getString("surveyor_info", null);
        if (json != null) {
            return gson.fromJson(json, SurveyorInfoFromAPI.class);
        }
        return null;
    }

    public void putSurveyorInfo(SurveyorInfoFromAPI surveyorInfoFromAPI) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("surveyor_info", surveyorInfoFromAPI.toJson().toString());
        editor.apply();
    }

    public void removeSurveyorInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("surveyor_info");
        editor.apply();
    }
}
