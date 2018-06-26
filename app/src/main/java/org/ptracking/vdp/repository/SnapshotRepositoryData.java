package org.ptracking.vdp.repository;

import org.ptracking.vdp.filestorage.modals.SnapshotsInfo;
import org.ptracking.vdp.modals.Survey;

public class SnapshotRepositoryData {
    private Survey survey;
    private SnapshotsInfo.Snapshot snapshot;

    public SnapshotRepositoryData(Survey survey, SnapshotsInfo.Snapshot snapshot) {
        this.survey = survey;
        this.snapshot = snapshot;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public SnapshotsInfo.Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(SnapshotsInfo.Snapshot snapshot) {
        this.snapshot = snapshot;
    }
}