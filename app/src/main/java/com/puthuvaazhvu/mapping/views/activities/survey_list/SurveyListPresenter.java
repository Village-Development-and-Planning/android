package com.puthuvaazhvu.mapping.views.activities.survey_list;

import android.content.Context;
import android.content.SharedPreferences;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.filestorage.DataInfoIO;
import com.puthuvaazhvu.mapping.filestorage.modals.AnswerInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SnapshotsInfo;
import com.puthuvaazhvu.mapping.filestorage.modals.SurveysInfo;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyListPresenter implements Contract.UserAction {
    private final Contract.View callback;
    private SharedPreferences sharedPreferences;
    private Context context;
    private DataInfoIO dataInfoIO;

    public SurveyListPresenter(
            Contract.View callback,
            SharedPreferences sharedPreferences
    ) {
        this.callback = callback;
        this.sharedPreferences = sharedPreferences;
        this.context = (Context) callback;
        dataInfoIO = new DataInfoIO();
    }

    @Override
    public void getSurveyData(SurveyListData surveyListData) {
        callback.showLoading(R.string.loading);

        SurveyDataRepository surveyDataRepository = new SurveyDataRepository(
                sharedPreferences,
                context,
                surveyListData.getId()
        );

        surveyDataRepository.get(Utils.isNetworkAvailable(context))
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
    public void fetchSurveys() {
        callback.showLoading(R.string.loading);

        dataInfoIO.read()
                .map(new Function<DataInfo, ArrayList<SurveyListData>>() {
                    @Override
                    public ArrayList<SurveyListData> apply(DataInfo dataInfo) throws Exception {
                        ArrayList<SurveyListData> data = new ArrayList<>();

                        AnswerInfo answersInfo = dataInfo.getAnswersInfo();
                        SnapshotsInfo snapshotsInfo = dataInfo.getSnapshotsInfo();
                        SurveysInfo surveysInfo = dataInfo.getSurveysInfo();

                        for (SurveysInfo.Survey survey : surveysInfo.getSurveys()) {

                            // calculate answers count
                            int answersCount = answersInfo.getAnswersCount(survey.getSurveyID());
                            String snapshotPath = null;

                            SnapshotsInfo.Survey s = snapshotsInfo.getSurvey(survey.getSurveyID());
                            if (s != null) {
                                SnapshotsInfo.Snapshot snapshot = s.getLatestLoggedSnapshot();
                                snapshotPath = snapshot.getPathToLastQuestion();
                            }

                            SurveyListData surveyListData = new SurveyListData(
                                    survey.getSurveyID(),
                                    survey.getSurveyName(),
                                    answersCount,
                                    snapshotPath != null,
                                    snapshotPath
                            );

                            data.add(surveyListData);
                        }

                        return data;
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
