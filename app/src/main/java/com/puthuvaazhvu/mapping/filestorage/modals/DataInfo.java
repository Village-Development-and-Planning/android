package com.puthuvaazhvu.mapping.filestorage.modals;

import com.puthuvaazhvu.mapping.other.Config;

/**
 * Created by muthuveerappans on 16/02/18.
 */

public class DataInfo {
    private int version;
    private SurveysInfo surveysInfo;
    private SnapshotsInfo snapshotsInfo;
    private SurveysInfo answersInfo;

    public DataInfo() {
        version = Config.Versions.DATA_INFO_VERSION;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public SurveysInfo getSurveysInfo() {
        return surveysInfo;
    }

    public void setSurveysInfo(SurveysInfo surveysInfo) {
        this.surveysInfo = surveysInfo;
    }

    public SnapshotsInfo getSnapshotsInfo() {
        return snapshotsInfo;
    }

    public void setSnapshotsInfo(SnapshotsInfo snapshotsInfo) {
        this.snapshotsInfo = snapshotsInfo;
    }

    public SurveysInfo getAnswersInfo() {
        return answersInfo;
    }

    public void setAnswersInfo(SurveysInfo answersInfo) {
        this.answersInfo = answersInfo;
    }
}
