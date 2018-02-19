package com.puthuvaazhvu.mapping.application.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;

/**
 * Created by muthuveerappans on 11/11/17.
 */

public class ApplicationData {
    private static ApplicationData applicationData;

    public static ApplicationData getInstance() {
        if (applicationData == null) {
            applicationData = new ApplicationData();
        }
        return applicationData;
    }

    private Survey survey;
    private String surveySnapShotPath;
    private JsonObject authJson;

    private ApplicationData() {

    }

    public static ApplicationData getApplicationData() {
        return applicationData;
    }

    public static void setApplicationData(ApplicationData applicationData) {
        ApplicationData.applicationData = applicationData;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public String getSurveySnapShotPath() {
        return surveySnapShotPath;
    }

    public void setSurveySnapShotPath(String surveySnapShotPath) {
        this.surveySnapShotPath = surveySnapShotPath;
    }

    public Survey getSurvey() {
        return survey;
    }

    public JsonObject getAuthJson() {
        return authJson;
    }

    public void setAuthJson(JsonObject authJson) {
        this.authJson = authJson;
    }
}
