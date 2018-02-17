package com.puthuvaazhvu.mapping.views.activities.dump_survey_activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.SurveyAPIInfo;
import com.puthuvaazhvu.mapping.network.APIUtils;
import com.puthuvaazhvu.mapping.network.implementations.ListSurveysAPI;
import com.puthuvaazhvu.mapping.network.implementations.SingleSurveyAPI;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class Presenter implements Contract.UserAction {
    private final Contract.View viewCallbacks;
    private ListSurveysAPI listSurveysAPI;
    private SharedPreferences sharedPreferences;
    private Context context;

    public Presenter(SharedPreferences sharedPreferences, Contract.View view) {
        this.viewCallbacks = view;
        listSurveysAPI = ListSurveysAPI.getInstance(APIUtils.getAuth(sharedPreferences));
        this.sharedPreferences = sharedPreferences;
        this.context = (Context) view;
    }

    @Override
    public void fetchListOfSurveys() {

        viewCallbacks.showLoading(R.string.loading);

        listSurveysAPI.getSurveysList()
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
    public void save(List<SurveyInfoData> surveyInfoData) {
        viewCallbacks.showLoading(R.string.loading);

        List<Observable<Survey>> observables = new ArrayList<>();

        for (SurveyInfoData d : surveyInfoData) {
            SurveyDataRepository surveyDataRepository = new SurveyDataRepository(sharedPreferences, context, d.id);
            observables.add(surveyDataRepository.get(false));
        }

        Observable.merge(observables)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Survey>() {
                    @Override
                    public void accept(Survey survey) throws Exception {
                        Timber.i("Saved survey " + survey.getId());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable.getMessage());
                        viewCallbacks.onError(R.string.error_saving_survey);
                        viewCallbacks.hideLoading();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        viewCallbacks.hideLoading();
                        viewCallbacks.finishActivity();
                    }
                });
    }
}
