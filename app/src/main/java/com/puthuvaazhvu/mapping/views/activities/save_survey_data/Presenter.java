package com.puthuvaazhvu.mapping.views.activities.save_survey_data;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.SurveyInfo;
import com.puthuvaazhvu.mapping.network.APIError;
import com.puthuvaazhvu.mapping.network.APIs;
import com.puthuvaazhvu.mapping.network.implementations.ListSurveysAPI;
import com.puthuvaazhvu.mapping.network.implementations.SingleSurveyAPI;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SavedSurveyInfoFileDataModal;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class Presenter implements Contract.UserAction {
    private final ListSurveysAPI surveysAPI;
    private final SingleSurveyAPI singleSurveyAPI;

    private final SaveToFile saveToFile;
    private final Contract.View viewCallbacks;

    private final Handler uiHandler;

    private final GetFromFile getFromFile;
    private final SurveyInfoFile savedSurveyInfoFile;

    public Presenter(SharedPreferences sharedPreferences, Contract.View view) {
        saveToFile = SaveToFile.getInstance();

        surveysAPI = ListSurveysAPI.getInstance(APIs.getAuth(sharedPreferences));
        singleSurveyAPI = SingleSurveyAPI.getInstance(APIs.getAuth(sharedPreferences));

        this.viewCallbacks = view;

        uiHandler = new Handler(Looper.getMainLooper());

        this.getFromFile = GetFromFile.getInstance();
        this.savedSurveyInfoFile = new SurveyInfoFile(getFromFile, saveToFile);
    }

    @Override
    public void fetchListOfSurveys() {

        viewCallbacks.showLoading(R.string.loading);

        surveysAPI.getSurveysList(new ListSurveysAPI.ListSurveysAPICallbacks() {
            @Override
            public void onSurveysLoaded(List<SurveyInfo> surveyInfoList) {
                viewCallbacks.onSurveyInfoFetched(SurveyInfoData.adapter(surveyInfoList));
                viewCallbacks.hideLoading();
            }

            @Override
            public void onErrorOccurred(APIError error) {
                Timber.e("Error occurred while fetching survey list data. " + error.message());
                viewCallbacks.hideLoading();
                viewCallbacks.onError(R.string.cannot_get_data);
            }
        });
    }

    @Override
    public void saveSurveyInfoToFile(List<SurveyInfoData> surveyInfoData) {
        viewCallbacks.showLoading(R.string.loading);
        _saveSurveyInfoToFile(surveyInfoData);
    }

    public void _saveSurveyInfoToFile(final List<SurveyInfoData> surveyInfoData) {
        ArrayList<Single<Optional>> observables = new ArrayList<>();

        for (SurveyInfoData d : surveyInfoData) {

            final SurveyInfoData infoFileData = d;
            final String id = d.id;

            Single<Optional> singleObservable = singleSurveyAPI.getSurvey(id)
                    .flatMap(new Function<String, SingleSource<? extends Optional>>() {
                        @Override
                        public SingleSource<? extends Optional> apply(@NonNull String data) throws Exception {
                            File file = DataFileHelpers.getSurveyDataFile(id, false);

                            if (file != null && file.exists()) {
                                return saveToFile.execute(data, file)
                                        .flatMap(new Function<Optional, SingleSource<Optional>>() {
                                            @Override
                                            public SingleSource<Optional> apply(@NonNull Optional optional) throws Exception {
                                                return savedSurveyInfoFile.updateListOfSurveys(
                                                        SavedSurveyInfoFileDataModal.adapter(infoFileData)
                                                );
                                            }
                                        });
                            } else {
                                Timber.e("Error creating the survey data file");
                                throw new Exception("Error creating the survey data file");
                            }
                        }
                    });

            observables.add(singleObservable);
        }

        Single.merge(observables)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Optional>() {
                    @Override
                    public void accept(@NonNull Optional optional) throws Exception {
                        viewCallbacks.hideLoading();
                        viewCallbacks.finishActivity();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Timber.e(throwable.getMessage());
                        viewCallbacks.onError(R.string.error_saving_survey);
                        viewCallbacks.hideLoading();
                    }
                });
    }
}
