package com.puthuvaazhvu.mapping.utils.saving.modals;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class SurveyInfo {
    private int version;
    private ArrayList<Survey> surveys;
    private transient Gson gson = new Gson();

    public SurveyInfo() {
        surveys = new ArrayList<>();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ArrayList<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(ArrayList<Survey> surveys) {
        this.surveys = surveys;
    }

    public String getAsJsonString() {
        return gson.toJson(this);
    }

    public Survey getSurvey(String id) {
        for (Survey survey : surveys) {
            if (survey.getSurveyID().equals(id)) {
                return survey;
            }
        }
        return null;
    }

    public static class Survey {
        private String surveyID;
        private String surveyName;
        private long timeStamp;

        public String getSurveyID() {
            return surveyID;
        }

        public void setSurveyID(String surveyID) {
            this.surveyID = surveyID;
        }

        public String getSurveyName() {
            return surveyName;
        }

        public void setSurveyName(String surveyName) {
            this.surveyName = surveyName;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
}
