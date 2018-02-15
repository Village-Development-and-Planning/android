package com.puthuvaazhvu.mapping.utils.saving.modals;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class AnswersInfo {
    private int version;
    private ArrayList<Survey> surveys;

    private transient Gson gson = new Gson();

    public AnswersInfo() {
        surveys = new ArrayList<>();
    }

    public int getVersion() {
        return version;
    }

    public ArrayList<Survey> getSurveys() {
        return surveys;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setSurveys(ArrayList<Survey> surveys) {
        this.surveys = surveys;
    }

    public boolean isSurveyPresent(String surveyID) {
        return getSurvey(surveyID) != null;
    }

    public Survey getSurvey(String surveyID) {
        for (int i = 0; i < surveys.size(); i++) {
            Survey survey = surveys.get(i);
            if (survey.getSurveyID().equals(surveyID))
                return survey;
        }
        return null;
    }

    public String toJsonString() {
        return gson.toJson(this);
    }

    public static class Survey {
        private String surveyID;
        private String surveyName;
        private ArrayList<Snapshot> snapshots;

        public String getSurveyID() {
            return surveyID;
        }

        public String getSurveyName() {
            return surveyName;
        }

        public void setSurveyName(String surveyName) {
            this.surveyName = surveyName;
        }

        public ArrayList<Snapshot> getSnapshots() {
            return snapshots;
        }

        public void setSurveyID(String surveyID) {
            this.surveyID = surveyID;
        }

        public void setSnapshots(ArrayList<Snapshot> snapshots) {
            this.snapshots = snapshots;
        }

        public int getCountOfCompletedSnapShots() {
            int count = 0;
            for (Snapshot snapshot : snapshots) {
                if (snapshot.isComplete()) count += 1;
            }
            return count;
        }

        public boolean isSurveyOngoing() {
            return !getLatestLoggedSnapshot().isComplete();
        }

        public Snapshot getLatestLoggedSnapshot() {
            Snapshot snapshot = snapshots.get(0);
            for (Snapshot s : snapshots) {
                if (snapshot.getTimestamp() < s.getTimestamp())
                    snapshot = s;
            }
            return snapshot;
        }
    }

    public static class Snapshot {
        private String snapshotFileName;
        private String pathToLastQuestion;
        private boolean isComplete;
        private long timestamp;
        private String pathToFile;

        public static String getSurveyID(String snapshotFileName) {
            return snapshotFileName.split("_")[0];
        }

        public String getPathToFile() {
            return pathToFile;
        }

        public void setPathToFile(String pathToFile) {
            this.pathToFile = pathToFile;
        }

        public String getSnapshotFileName() {
            return snapshotFileName;
        }

        public String getPathToLastQuestion() {
            return pathToLastQuestion;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setSnapshotFileName(String snapshotFileName) {
            this.snapshotFileName = snapshotFileName;
        }

        public void setPathToLastQuestion(String pathToLastQuestion) {
            this.pathToLastQuestion = pathToLastQuestion;
        }

        public void setComplete(boolean complete) {
            this.isComplete = complete;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
