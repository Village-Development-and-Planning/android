package org.ptracking.vdp.filestorage.modals;

/**
 * Created by muthuveerappans on 05/06/18.
 */

public class SurveyorData {
    private SurveysInfo surveysInfo;
    private SnapshotsInfo snapshotsInfo;
    private AnswerInfo answersInfo;

    public SurveyorData() {
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
