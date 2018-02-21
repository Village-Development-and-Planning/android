package com.puthuvaazhvu.mapping.views.activities.survey_list;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyListData {

    private String id;
    private String name;
    private boolean isChecked;
    private int count;
    private boolean isOngoing;
    private String snapshotPath; // will be null if the survey is new
    private String snapshotID;

    public SurveyListData(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SurveyListData(String id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.isOngoing = false;
    }

    public SurveyListData(String id, String name, int count, boolean isOngoing, String snapshotPath, String snapshotID) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.isOngoing = isOngoing;
        this.snapshotPath = snapshotPath;
        this.snapshotID = snapshotID;
    }

    public String getSnapshotID() {
        return snapshotID;
    }

    public void setSnapshotID(String snapshotID) {
        this.snapshotID = snapshotID;
    }

    public String getSnapshotPath() {
        return snapshotPath;
    }

    public void setSnapshotPath(String snapshotPath) {
        this.snapshotPath = snapshotPath;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isOngoing() {
        return isOngoing;
    }

    public void setOngoing(boolean ongoing) {
        isOngoing = ongoing;
    }
}
