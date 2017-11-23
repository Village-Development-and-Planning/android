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
    private final AnswerDataModal.Snapshot snapshot;

    private STATUS status;

    public SurveyListData(String id, String name, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.isChecked = isChecked;
        this.status = STATUS.NOT_STARTED;
        snapshot = null;
    }

    public SurveyListData(String id, String name, boolean isChecked, AnswerDataModal.Snapshot snapshot, STATUS status) {
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

    public AnswerDataModal.Snapshot getSnapshot() {
        return snapshot;
    }
}
