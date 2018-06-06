package com.puthuvaazhvu.mapping.data;

import android.content.Context;

import com.puthuvaazhvu.mapping.filestorage.io.DataInfoIO;
import com.puthuvaazhvu.mapping.filestorage.io.SnapshotIO;
import com.puthuvaazhvu.mapping.filestorage.modals.AnswerInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveyorInfo;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.ThrowableWithErrorCode;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 06/06/18.
 */

public class SnapshotRepository extends Repository<Survey> {
    private final SnapshotIO snapshotIO;
    private final DataInfoIO dataInfoIO;

    public SnapshotRepository(Context context, String surveyorCode, String password) {
        super(context, surveyorCode, password);
        snapshotIO = new SnapshotIO();
        dataInfoIO = new DataInfoIO();
    }

    @Override
    public Observable<Survey> get(boolean forceOffline) {
        throw new RuntimeException("Not implemented");
    }

    public Observable<Survey> get(final String surveyId) {
        return dataInfoIO.read()
                .flatMap(new Function<DataInfo, ObservableSource<Survey>>() {
                    @Override
                    public ObservableSource<Survey> apply(DataInfo dataInfo) throws Exception {
                        SurveyorInfo surveyorInfo = dataInfo.getSurveyorInfo(getSurveyorCode());
                        if (surveyorInfo != null) {
                            SnapshotsInfo snapshotsInfo = surveyorInfo.getSnapshotsInfo();
                            SnapshotsInfo.Survey survey = snapshotsInfo.getSurvey(surveyId);

                            if (survey != null) {
                                SnapshotsInfo.Snapshot snapshot = survey.getLatestLoggedSnapshot();
                                return snapshotIO.read(snapshot.getSnapshotID());
                            }
                        }
                        throw new Exception(new ThrowableWithErrorCode("No snapshot found."
                                , Constants.ErrorCodes.FILE_NOT_EXIST));
                    }
                });
    }
}
