package org.ptracking.vdp.filestorage.modals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class SurveysInfo implements Serializable {

    private ArrayList<Survey> surveys;

    public SurveysInfo() {
        surveys = new ArrayList<>();
    }

    public ArrayList<Survey> getSurveys() {
        return surveys;
    }


    public void addSurvey(Survey survey) {
        Iterator<Survey> surveyIterator = surveys.iterator();
        while (surveyIterator.hasNext()) {
            if (surveyIterator.next().getSurveyID().equals(survey.getSurveyID())) {
                surveyIterator.remove();
            }
        }

        surveys.add(survey);
    }

    public void setSurveys(ArrayList<Survey> surveys) {
        this.surveys = surveys;
    }

    public Survey getSurvey(String id) {
        for (Survey survey : surveys) {
            if (survey.getSurveyID().equals(id)) {
                return survey;
            }
        }
        return null;
    }

    public boolean removeSurvey(String id) {
        Iterator<Survey> surveyIterator = surveys.iterator();
        while (surveyIterator.hasNext()) {
            if (surveyIterator.next().getSurveyID().equals(id)) {
                surveyIterator.remove();
                return true;
            }
        }
        return false;
    }

    public static class Survey implements Serializable {
        private String surveyID;
        private String surveyName;
        private String filename;
        private long timeStamp;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

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