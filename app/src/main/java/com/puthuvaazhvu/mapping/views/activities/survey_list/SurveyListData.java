package com.puthuvaazhvu.mapping.views.activities.survey_list;

import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswerDataModal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyListData {

    public enum STATUS {
        COMPLETED, NOT_STARTED, ONGOING
    }

    private final String id;
    private final String name;
    private boolean isChecked;
    private final SurveySnapShot snapshot;

    private STATUS status;

    public SurveyListData(String id, String name, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.isChecked = isChecked;
        this.status = STATUS.NOT_STARTED;
        snapshot = null;
    }

    public SurveyListData(String id, String name, boolean isChecked, SurveySnapShot snapshot, STATUS status) {
        this.id = id;
        this.name = name;
        this.isChecked = isChecked;
        this.snapshot = snapshot;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public SurveySnapShot getSurveySnapshot() {
        return snapshot;
    }

    public static class SurveySnapShot {
        private final String surveyID;
        private final String snapshotID;
        private final String path;

        public SurveySnapShot(String snapshotID, String path) {
            this.surveyID = snapshotID.split("_")[0];
            this.snapshotID = snapshotID;
            this.path = path;
        }

        public String getSurveyID() {
            return surveyID;
        }

        public String getSnapshotID() {
            return snapshotID;
        }

        public String getPath() {
            return path;
        }

        public static SurveySnapShot adapter(AnswerDataModal.Snapshot snapshot) {
            return new SurveySnapShot(snapshot.getSnapshotId(), snapshot.getPathToLastQuestion());
        }
    }
}
