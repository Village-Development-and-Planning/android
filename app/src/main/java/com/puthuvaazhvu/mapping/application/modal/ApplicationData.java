package com.puthuvaazhvu.mapping.application.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;

/**
 * Created by muthuveerappans on 11/11/17.
 */

// Todo: refactor this
public class ApplicationData implements Parcelable {
    private static ApplicationData applicationData;

    public static ApplicationData getInstance() {
        if (applicationData == null) {
            applicationData = new ApplicationData();
        }
        return applicationData;
    }

    private Survey survey;
    private SurveySnapShot surveySnapShot;
    private JsonObject authJson;

    private ApplicationData() {
        surveySnapShot = new SurveySnapShot();
    }

    public String getSnapshotPath() {
        return surveySnapShot.pathToLastAnsweredQuestion;
    }

    public String getSnapShotID() {
        return surveySnapShot.snapShotID;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey, String snapShotID, String path) {
        this.survey = survey;
        setSurveySnapShot(snapShotID, path);
    }

    private void setSurveySnapShot(String snapShotID, String path) {
        if (snapShotID == null || path == null) {
            surveySnapShot.surveyID = null;
            surveySnapShot.pathToLastAnsweredQuestion = null;
            return;
        }

        String surveyID = snapShotID.split("_")[0];

        if (!survey.getId().equals(surveyID)) {
            throw new IllegalArgumentException("The snapshot id should be " +
                    "the same as the survey's id. " +
                    "Update the survey to a different one or give another snapshot of the current survey.");
        }

        surveySnapShot.surveyID = surveyID;
        surveySnapShot.pathToLastAnsweredQuestion = path;
        surveySnapShot.snapShotID = snapShotID;
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

    private static class SurveySnapShot {
        public String surveyID;
        public String snapShotID;
        public String pathToLastAnsweredQuestion;
    }
}
