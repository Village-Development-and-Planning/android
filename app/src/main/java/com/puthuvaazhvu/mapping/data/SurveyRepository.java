package com.puthuvaazhvu.mapping.data;

import android.content.Context;

import com.puthuvaazhvu.mapping.filestorage.io.SurveyIO;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.surveyorinfo.SurveyorInfo;
import com.puthuvaazhvu.mapping.network.implementations.SingleSurveyAPI;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.ThrowableWithErrorCode;
import com.puthuvaazhvu.mapping.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

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

    private Observable<SurveyorInfo> getSurveyorInfo() {
        return authRepository.get(false);
    }

    private Observable<Survey> getSurveyFromAPI(String surveyId) {
        return singleSurveyAPI.getSurvey(surveyId)
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
        return getSurveyorInfo().flatMap(new Function<SurveyorInfo, ObservableSource<Survey>>() {
            @Override
            public ObservableSource<Survey> apply(final SurveyorInfo surveyorInfo) throws Exception {
                return getSurveyOffline(surveyorInfo.getSurveyId())
                        .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Survey>>() {
                            @Override
                            public ObservableSource<? extends Survey> apply(Throwable throwable) throws Exception {
                                if (Utils.isNetworkAvailable(getContext())) {
                                    return getSurveyFromAPI(surveyorInfo.getSurveyId());
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
