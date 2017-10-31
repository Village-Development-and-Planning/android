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
import com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private final SurveyInfoFile surveyInfoFile;

    public Presenter(SharedPreferences sharedPreferences, Contract.View view) {
        saveToFile = SaveToFile.getInstance();

        surveysAPI = ListSurveysAPI.getInstance(APIs.getAuth(sharedPreferences));
        singleSurveyAPI = SingleSurveyAPI.getInstance(APIs.getAuth(sharedPreferences));

        this.viewCallbacks = view;

        uiHandler = new Handler(Looper.getMainLooper());

        this.getFromFile = GetFromFile.getInstance();
        this.surveyInfoFile = new SurveyInfoFile(getFromFile, saveToFile);
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

        SaveTask saveTask = new SaveTask(surveyInfoData);
        new Thread(saveTask).start();
    }

    private class SaveTask implements Runnable {
        private final List<SurveyInfoData> surveyInfoData;

        private final ArrayList<String> savedSurveyIds = new ArrayList<>();
        private final ArrayList<String> errorSurveyIds = new ArrayList<>();

        private final ExecutorService pool = Executors.newCachedThreadPool();

        public SaveTask(List<SurveyInfoData> surveyInfoData) {
            this.surveyInfoData = surveyInfoData;
        }

        @Override
        public void run() {
            try {

                ArrayList<Callable<Void>> saveToFileCallable = new ArrayList<>();

                for (SurveyInfoData d : surveyInfoData) {
                    String surveyID = d.id;
                    String survey = singleSurveyAPI.getSurveySynchronous(surveyID);

                    if (survey != null) {
                        File file = DataFileHelpers.getSurveyDataFile(surveyID, false);

                        if (file != null && file.exists()) {
                            saveToFileCallable.add(saveToFile.execute(survey, file));
                            savedSurveyIds.add(surveyID);
                        } else {
                            Timber.e("Error creating the survey data file");
                            errorSurveyIds.add(surveyID);
                        }

                    } else {
                        errorSurveyIds.add(surveyID);
                    }
                }

                // add this in the last after the survey folder is populated
                saveToFileCallable.add(surveyInfoFile.updateListOfSurveys(savedSurveyIds));

                pool.invokeAll(saveToFileCallable);

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        viewCallbacks.hideLoading();

                        if (errorSurveyIds.isEmpty())
                            viewCallbacks.finishActivity();
                        else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Error saving ");
                            for (String e : errorSurveyIds) {
                                stringBuilder.append(e);
                                stringBuilder.append(" ");
                            }
                            Timber.e(stringBuilder.toString());
                            viewCallbacks.onError(R.string.error_saving_survey);
                        }
                    }
                });

                return;

            } catch (IOException e) {
                Timber.e("Error getting a survey. " + e.getMessage());
            } catch (InterruptedException e) {
                Timber.e("Error getting a survey. " + e.getMessage());
            } catch (ExecutionException e) {
                Timber.e("Error getting a survey. " + e.getMessage());
            }

            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    viewCallbacks.hideLoading();
                }
            });
        }
    }
}
