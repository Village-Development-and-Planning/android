package com.puthuvaazhvu.mapping.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.puthuvaazhvu.mapping.other.Constants;

import io.reactivex.Observable;

/**
 * Created by muthuveerappans on 17/02/18.
 */

public abstract class Repository<T> {
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final String surveyorCode;
    private final String password;

    Repository(Context context, String surveyorCode, String password) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        this.surveyorCode = surveyorCode;
        this.password = password;
    }

    public String getSurveyorCode() {
        return surveyorCode;
    }

    public String getPassword() {
        return password;
    }

    public abstract Observable<T> get(boolean forceOffline);

    public Context getContext() {
        return context;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
