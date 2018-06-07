package com.puthuvaazhvu.mapping.views.activities.home;

import android.content.Context;

import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.surveyorinfo.SurveyorInfoFromAPI;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.repository.SnapshotRepository;
import com.puthuvaazhvu.mapping.repository.SnapshotRepositoryData;
import com.puthuvaazhvu.mapping.repository.SurveyRepository;
import com.puthuvaazhvu.mapping.utils.SharedPreferenceUtils;
import com.puthuvaazhvu.mapping.views.activities.modals.CurrentSurveyInfo;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthuveerappans on 07/06/18.
 */

public class HomeActivityPresenter implements Contract.UserAction {
    private final SharedPreferenceUtils sharedPreferenceUtils;
    private final SurveyRepository surveyRepository;
    private final SnapshotRepository snapshotRepository;

    private final Contract.View context;
    private final SurveyorInfoFromAPI surveyorInfoFromAPI;

    public HomeActivityPresenter(Contract.View context) {
        this.context = context;

        Context c = (Context) context;

        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(c);
        surveyorInfoFromAPI = sharedPreferenceUtils.getSurveyorInfo();

        surveyRepository = new SurveyRepository(c, surveyorInfoFromAPI.getCode(), Constants.PASSWORD);
        snapshotRepository = new SnapshotRepository(c, surveyorInfoFromAPI.getCode(), Constants.PASSWORD);
    }

    @Override
    public void doLogout() {
        sharedPreferenceUtils.removeSurveyorInfo();
        context.onLogoutSuccessful();
    }

    @Override
    public void doUpload() {
        context.openUploadActivity(surveyorInfoFromAPI.getCode(), surveyorInfoFromAPI.getName());
    }

    @Override
    public void doDownload() {
        context.showLoading();

        surveyRepository.get(false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Survey>() {
                               @Override
                               public void accept(Survey survey) throws Exception {
                                   context.hideLoading();
                                   context.onSurveyDownloadedSuccessfully(surveyorInfoFromAPI.getCode(), survey);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                context.hideLoading();
                            }
                        });
    }

    @Override
    public void startSurvey() {
        context.showLoading();

        getSurveyToStart()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<CurrentSurveyInfo>() {
                               @Override
                               public void accept(CurrentSurveyInfo currentSurveyInfo) throws Exception {
                                   context.hideLoading();
                                   context.openMainActivity(currentSurveyInfo);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                context.hideLoading();
                            }
                        });
    }

    @Override
    public void getSurveyorInfo() {
        context.onSurveyorInfoFetched(surveyorInfoFromAPI);
    }

    private Observable<CurrentSurveyInfo> getSurveyToStart() {
        return snapshotRepository.get(true)
                .map(new Function<SnapshotRepositoryData, CurrentSurveyInfo>() {
                    @Override
                    public CurrentSurveyInfo apply(SnapshotRepositoryData snapshotRepositoryData) throws Exception {
                        return CurrentSurveyInfo.adapterSnapshotRepositoryData(snapshotRepositoryData);
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CurrentSurveyInfo>>() {
                    @Override
                    public ObservableSource<? extends CurrentSurveyInfo> apply(Throwable throwable) throws Exception {
                        return surveyRepository.get(false)
                                .map(new Function<Survey, CurrentSurveyInfo>() {
                                    @Override
                                    public CurrentSurveyInfo apply(Survey survey) throws Exception {
                                        return CurrentSurveyInfo.adapterSurvey(survey);
                                    }
                                });
                    }
                });
    }
}
