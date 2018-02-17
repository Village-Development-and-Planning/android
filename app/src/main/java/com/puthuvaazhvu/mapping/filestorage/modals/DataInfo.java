package com.puthuvaazhvu.mapping.filestorage.modals;

import com.puthuvaazhvu.mapping.other.Config;

import java.io.Serializable;

/**
 * Created by muthuveerappans on 16/02/18.
 */

public class DataInfo implements Serializable {
    private int version;
    private SurveysInfo surveysInfo;
    private SnapshotsInfo snapshotsInfo;
    private AnswerInfo answersInfo;

    public DataInfo() {
        version = Config.Versions.DATA_INFO_VERSION;
        surveysInfo = new SurveysInfo();
        snapshotsInfo = new SnapshotsInfo();
        answersInfo = new AnswerInfo();
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

    public AnswerInfo getAnswersInfo() {
        return answersInfo;
    }

    public void setAnswersInfo(AnswerInfo answersInfo) {
        this.answersInfo = answersInfo;
    }
}
