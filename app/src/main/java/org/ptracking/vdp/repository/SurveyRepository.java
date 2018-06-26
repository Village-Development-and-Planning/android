package org.ptracking.vdp.repository;

import android.content.Context;

import org.ptracking.vdp.filestorage.io.SurveyIO;
import org.ptracking.vdp.filestorage.modals.DataInfo;
import org.ptracking.vdp.modals.Survey;
import org.ptracking.vdp.modals.surveyorinfo.SurveyorInfoFromAPI;
import org.ptracking.vdp.network.implementations.SingleSurveyAPI;
import org.ptracking.vdp.other.Constants;
import org.ptracking.vdp.utils.ThrowableWithErrorCode;
import org.ptracking.vdp.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthuveerappans on 01/02/18.
 */

public class SurveyRepository extends Repository<Survey> {
    private final SingleSurveyAPI singleSurveyAPI;
    private final AuthRepository authRepository;
    private final SurveyIO surveyIO;

    public SurveyRepository(Context context, String surveyorCode, String password) {
        super(context, surveyorCode, password);

        singleSurveyAPI = new SingleSurveyAPI(surveyorCode, password);
        authRepository = new AuthRepository(context, surveyorCode, password);

        surveyIO = new SurveyIO();
    }

    private Observable<SurveyorInfoFromAPI> getSurveyorInfo() {
        return authRepository.get(false);
    }

    private Observable<Survey> getSurveyFromAPI(String surveyId) {
        return singleSurveyAPI.getSurvey(surveyId)
                .observeOn(Schedulers.io())
                .flatMap(new Function<Survey, ObservableSource<Survey>>() {
                    @Override
                    public ObservableSource<Survey> apply(final Survey survey) throws Exception {
                        return surveyIO.save(survey, getSurveyorCode())
                                .map(new Function<DataInfo, Survey>() {
                                    @Override
                                    public Survey apply(DataInfo dataInfo) throws Exception {
                                        return survey;
                                    }
                                });
                    }
                });
    }

    private Observable<Survey> getSurveyOffline(String surveyId) {
        return surveyIO.read(surveyId);
    }

    @Override
    public Observable<Survey> get(final boolean forceOffline) {
        return getSurveyorInfo()
                .observeOn(Schedulers.io())
                .flatMap(new Function<SurveyorInfoFromAPI, ObservableSource<Survey>>() {
                    @Override
                    public ObservableSource<Survey> apply(final SurveyorInfoFromAPI surveyorInfoFromAPI) throws Exception {
                        return getSurveyOffline(surveyorInfoFromAPI.getSurveyId())
                                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Survey>>() {
                                    @Override
                                    public ObservableSource<? extends Survey> apply(Throwable throwable) throws Exception {
                                        if (Utils.isNetworkAvailable(getContext())) {
                                            return getSurveyFromAPI(surveyorInfoFromAPI.getSurveyId());
                                        } else {
                                            throw new Exception(new ThrowableWithErrorCode("Network unavailable"
                                                    , Constants.ErrorCodes.NETWORK_ERROR));
                                        }
                                    }
                                });
                    }
                });
    }
}
