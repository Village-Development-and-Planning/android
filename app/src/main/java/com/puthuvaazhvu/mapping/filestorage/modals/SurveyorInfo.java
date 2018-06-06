package com.puthuvaazhvu.mapping.filestorage.modals;

import com.puthuvaazhvu.mapping.other.Config;

/**
 * Created by muthuveerappans on 05/06/18.
 */

public class SurveyorInfo {
    private SurveysInfo surveysInfo;
    private SnapshotsInfo snapshotsInfo;
    private AnswerInfo answersInfo;

    public SurveyorInfo() {
        surveysInfo = new SurveysInfo();
        snapshotsInfo = new SnapshotsInfo();
        answersInfo = new AnswerInfo();
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
