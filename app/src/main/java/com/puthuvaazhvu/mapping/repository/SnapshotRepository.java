package com.puthuvaazhvu.mapping.repository;

import android.content.Context;

import com.puthuvaazhvu.mapping.filestorage.io.DataInfoIO;
import com.puthuvaazhvu.mapping.filestorage.io.SnapshotIO;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveyorData;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.surveyorinfo.SurveyorInfoFromAPI;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.ThrowableWithErrorCode;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthuveerappans on 06/06/18.
 */

public class SnapshotRepository extends Repository<SnapshotRepositoryData> {
    private final SnapshotIO snapshotIO;
    private final DataInfoIO dataInfoIO;
    private final AuthRepository authRepository;

    public SnapshotRepository(Context context, String surveyorCode, String password) {
        super(context, surveyorCode, password);
        snapshotIO = new SnapshotIO();
        dataInfoIO = new DataInfoIO();
        authRepository = new AuthRepository(context, surveyorCode, password);
    }

    @Override
    public Observable<SnapshotRepositoryData> get(boolean forceOffline) {
        return authRepository.get(false)
                .observeOn(Schedulers.io())
                .flatMap(new Function<SurveyorInfoFromAPI, ObservableSource<SnapshotRepositoryData>>() {
                    @Override
                    public ObservableSource<SnapshotRepositoryData> apply(final SurveyorInfoFromAPI surveyorInfoFromAPI) throws Exception {
                        return dataInfoIO.read()
                                .flatMap(new Function<DataInfo, ObservableSource<SnapshotRepositoryData>>() {
                                    @Override
                                    public ObservableSource<SnapshotRepositoryData> apply(DataInfo dataInfo) throws Exception {
                                        SurveyorData surveyorDataOffline = dataInfo.getSurveyorData(getSurveyorCode());
                                        if (surveyorDataOffline != null) {
                                            SnapshotsInfo snapshotsInfo = surveyorDataOffline.getSnapshotsInfo();
                                            SnapshotsInfo.Survey survey = snapshotsInfo.getSurvey(surveyorInfoFromAPI.getSurveyId());

                                            if (survey != null) {
                                                final SnapshotsInfo.Snapshot snapshot = survey.getLatestLoggedSnapshot();
                                                return snapshotIO.read(snapshot.getSnapshotID())
                                                        .map(new Function<Survey, SnapshotRepositoryData>() {
                                                            @Override
                                                            public SnapshotRepositoryData apply(Survey survey) throws Exception {
                                                                return new SnapshotRepositoryData(survey, snapshot);
                                                            }
                                                        });
                                            }
                                        }
                                        throw new Exception(new ThrowableWithErrorCode("No snapshot found."
                                                , Constants.ErrorCodes.FILE_NOT_EXIST));
                                    }
                                });
                    }
                });
    }
}
