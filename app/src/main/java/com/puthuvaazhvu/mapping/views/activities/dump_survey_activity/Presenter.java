package com.puthuvaazhvu.mapping.views.activities.dump_survey_activity;

import android.content.SharedPreferences;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.SurveyAPIInfo;
import com.puthuvaazhvu.mapping.network.APIUtils;
import com.puthuvaazhvu.mapping.network.implementations.SingleSurveyAPI;
import com.puthuvaazhvu.mapping.utils.saving.SurveyIOUtils;
import com.puthuvaazhvu.mapping.utils.saving.modals.SurveyInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class Presenter implements Contract.UserAction {
    private final Contract.View viewCallbacks;
    private SurveyIOUtils surveyIOUtils;
    private SurveyDataRepository surveyDataRepository;
    private SingleSurveyAPI singleSurveyAPI;

    public Presenter(SharedPreferences sharedPreferences, Contract.View view) {
        this.viewCallbacks = view;
        surveyIOUtils = SurveyIOUtils.getInstance();
        surveyDataRepository = SurveyDataRepository.getInstance(sharedPreferences);
        singleSurveyAPI = SingleSurveyAPI.getInstance(APIUtils.getAuth(sharedPreferences));
    }

    @Override
    public void fetchListOfSurveys() {

        viewCallbacks.showLoading(R.string.loading);

        surveyDataRepository.getSurveysFromAPI()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<SurveyAPIInfo>>() {
                    @Override
                    public void accept(List<SurveyAPIInfo> surveyInfoList) throws Exception {
                        viewCallbacks.onSurveyInfoFetched(SurveyInfoData.adapter(surveyInfoList));
                        viewCallbacks.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        viewCallbacks.hideLoading();
                        viewCallbacks.onError(R.string.cannot_get_data);
                    }
                });
    }

    @Override
    public void saveSurveyInfoToFile(List<SurveyInfoData> surveyInfoData) {
        viewCallbacks.showLoading(R.string.loading);

        List<Observable<SurveyInfo>> observables = new ArrayList<>();

        for (SurveyInfoData d : surveyInfoData) {
            observables.add(
                    singleSurveyAPI.getSurvey(d.id)
                            .flatMap(new Function<String, ObservableSource<SurveyInfo>>() {
                                @Override
                                public ObservableSource<SurveyInfo> apply(String s) throws Exception {
                                    return surveyIOUtils.saveSurvey(s);
                                }
                            })
            );
        }

        Observable.zip(observables, new Function<Object[], Boolean>() {
            @Override
            public Boolean apply(Object[] objects) throws Exception {
                return true;
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean b) throws Exception {
                                   viewCallbacks.hideLoading();
                                   viewCallbacks.finishActivity();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Timber.e(throwable.getMessage());
                                   viewCallbacks.onError(R.string.error_saving_survey);
                                   viewCallbacks.hideLoading();
                               }
                           }
                );
    }
}
