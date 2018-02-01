package com.puthuvaazhvu.mapping.application.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;

/**
 * Created by muthuveerappans on 11/11/17.
 */

public class ApplicationData implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.survey, flags);
    }

    protected ApplicationData(Parcel in) {
        this.survey = in.readParcelable(Survey.class.getClassLoader());
    }

    public static final Creator<ApplicationData> CREATOR = new Creator<ApplicationData>() {
        @Override
        public ApplicationData createFromParcel(Parcel source) {
            return new ApplicationData(source);
        }

        @Override
        public ApplicationData[] newArray(int size) {
            return new ApplicationData[size];
        }
    };
}
