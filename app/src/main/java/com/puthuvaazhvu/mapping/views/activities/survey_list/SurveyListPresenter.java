package com.puthuvaazhvu.mapping.views.activities.survey_list;

import android.content.SharedPreferences;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.saving.AnswerIOUtils;
import com.puthuvaazhvu.mapping.utils.saving.SurveyIOUtils;
import com.puthuvaazhvu.mapping.utils.saving.modals.AnswersInfo;
import com.puthuvaazhvu.mapping.utils.saving.modals.SurveyInfo;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyListPresenter implements Contract.UserAction {
    private final Contract.View callback;
    private AnswerIOUtils answerIOUtils;
    private SurveyIOUtils surveyIOUtils;
    private SurveyDataRepository surveyDataRepository;

    public SurveyListPresenter(
            Contract.View callback,
            SharedPreferences sharedPreferences
    ) {
        this.callback = callback;
        this.answerIOUtils = AnswerIOUtils.getInstance();
        this.surveyIOUtils = SurveyIOUtils.getInstance();
        this.surveyDataRepository = SurveyDataRepository.getInstance(sharedPreferences);
    }

    @Override
    public void getSurveyData(SurveyListData surveyListData) {
        callback.showLoading(R.string.loading);

        surveyDataRepository.getSurveyFromFile(
                surveyListData.getId(),
                surveyListData.getSnapshotPath(),
                surveyListData.getSnapShotFileName()
        )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Survey>() {
                    @Override
                    public void accept(Survey survey) throws Exception {
                        callback.hideLoading();
                        callback.onSurveyLoaded(survey);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e("Error while fetching survey from file " + throwable.getMessage());
                        callback.hideLoading();
                        callback.onError(R.string.err_no_data);
                    }
                });

    }


    @Override
    public void fetchListOfSurveys() {

        callback.showLoading(R.string.loading);

        Observable.zip(
                answerIOUtils.readAnswerInfoFile()
                        // http://reactivex.io/documentation/operators/catch.html
                        .onErrorReturn(new Function<Throwable, AnswersInfo>() {
                            @Override
                            public AnswersInfo apply(Throwable throwable) throws Exception {
                                return new AnswersInfo();
                            }
                        }),
                surveyIOUtils.readSurveysInfoFile()
                        .onErrorReturn(new Function<Throwable, SurveyInfo>() {
                            @Override
                            public SurveyInfo apply(Throwable throwable) throws Exception {
                                return new SurveyInfo();
                            }
                        }),
                new BiFunction<AnswersInfo, SurveyInfo, ArrayList<SurveyListData>>() {
                    @Override
                    public ArrayList<SurveyListData> apply(AnswersInfo answersInfo, SurveyInfo surveyInfo)
                            throws Exception {

                        ArrayList<SurveyListData> surveyListData = new ArrayList<>();

                        for (SurveyInfo.Survey s : surveyInfo.getSurveys()) {
                            Survey survey = surveyIOUtils.readSurvey(s.getSurveyID()).blockingFirst();

                            SurveyListData sld = new SurveyListData(survey.getId(), survey.getName());

                            if (answersInfo.isSurveyPresent(survey.getId())) {
                                AnswersInfo.Survey snapshots = answersInfo.getSurvey(survey.getId());

                                int completedCount = snapshots.getCountOfCompletedSnapShots();

                                sld.setCount(completedCount);
                                sld.setOngoing(snapshots.isSurveyOngoing());

                                if (snapshots.isSurveyOngoing()) {
                                    AnswersInfo.Snapshot snapshot = snapshots.getLatestLoggedSnapshot();
                                    sld.setSnapshotPath(snapshot.getPathToLastQuestion());
                                    sld.setSnapShotFileName(snapshot.getSnapshotFileName());
                                }
                            }

                            surveyListData.add(sld);
                        }

                        return surveyListData;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ArrayList<SurveyListData>>() {
                    @Override
                    public void accept(ArrayList<SurveyListData> surveyListData) throws Exception {
                        callback.hideLoading();
                        callback.onSurveysFetched(surveyListData);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.hideLoading();
                        Timber.e(throwable.getMessage());
                        callback.onError(R.string.err_no_data);
                    }
                });

    }
}
