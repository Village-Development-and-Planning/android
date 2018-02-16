package com.puthuvaazhvu.mapping.filestorage.modals;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class SnapshotsInfo {
    private ArrayList<Survey> surveys;

    public SnapshotsInfo() {
        surveys = new ArrayList<>();
    }

    public ArrayList<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(ArrayList<Survey> surveys) {
        this.surveys = surveys;
    }

    public boolean isSurveyPresent(String surveyID) {
        return getSurvey(surveyID) != null;
    }

    public Survey getSurveyFromFileName(String snapshotFileName) {
        return getSurvey(snapshotFileName.split("_")[0]);
    }

    public Survey getSurvey(String surveyID) {
        for (int i = 0; i < surveys.size(); i++) {
            Survey survey = surveys.get(i);
            if (survey.getSurveyID().equals(surveyID))
                return survey;
        }
        return null;
    }

    public void addSnapshot(Snapshot snapshot) {
        Survey s = getSurvey(snapshot.getSurveyID());
        if (s == null) {
            // survey not present
            Survey survey = new Survey();
            survey.addSnapshot(snapshot);
        } else {
            s.getSnapshots().add(snapshot);
        }
    }

    public static class Survey {
        private String surveyID;
        private String surveyName;
        private ArrayList<Snapshot> snapshots = new ArrayList<>();

        public void addSnapshot(Snapshot snapshot) {
            for (Snapshot s : snapshots) {
                if (s.getSnapshotFileName().equals(snapshot.getSnapshotFileName())) {
                    s.copy(snapshot);
                    return;
                }
            }
            snapshots.add(snapshot);
        }

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
        private long timestamp;

        public void copy(Snapshot other) {
            this.snapshotFileName = other.snapshotFileName;
            this.pathToLastQuestion = other.pathToLastQuestion;
            this.timestamp = other.timestamp;
        }

        public String getSurveyID() {
            return snapshotFileName.split("_")[0];
        }

        public String getSnapshotFileName() {
            return snapshotFileName;
        }

        public String getPathToLastQuestion() {
            return pathToLastQuestion;
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

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
