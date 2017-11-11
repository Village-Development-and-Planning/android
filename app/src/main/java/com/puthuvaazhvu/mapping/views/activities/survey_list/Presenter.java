package com.puthuvaazhvu.mapping.views.activities.survey_list;

import android.os.AsyncTask;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.DataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SavedSurveyInfoFileDataModal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
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
    private final com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile surveyInfoFile;
    private final Contract.View callback;

    public Presenter(SurveyInfoFile surveyInfoFile, Contract.View callback) {
        this.surveyInfoFile = surveyInfoFile;
        this.callback = callback;
    }

    @Override
    public void fetchListOfSurveys() {
        surveyInfoFile.getInfoJsonParsed()
                .map(new Function<SavedSurveyInfoFileDataModal, ArrayList<SurveyListData>>() {
                    @Override
                    public ArrayList<SurveyListData> apply(@NonNull SavedSurveyInfoFileDataModal savedSurveyInfoFileDataModal) throws Exception {

                        List<DataModal> surveyInfoFileDataList = savedSurveyInfoFileDataModal.getSurveyData();

                        ArrayList<SurveyListData> surveyListDataArrayList = new ArrayList<>();

                        if (surveyInfoFileDataList != null) {

                            for (DataModal surveyData : surveyInfoFileDataList) {
                                surveyListDataArrayList.add(SurveyListData.adapter(surveyData.get_id()
                                        , surveyData.getSurveyName()));
                            }
                        }

                        return surveyListDataArrayList;

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ArrayList<SurveyListData>>() {
                    @Override
                    public void accept(@NonNull ArrayList<SurveyListData> surveyListDataList) throws Exception {
                        callback.onSurveysFetched(surveyListDataList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        callback.onError(R.string.err_no_data);
                    }
                });
    }
}
