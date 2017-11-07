package com.puthuvaazhvu.mapping.views.activities.survey_list;

import android.os.AsyncTask;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.Data;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SavedSurveyInfoFileData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class Presenter implements Contract.UserAction {
    private final com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile surveyInfoFile;
    private final Contract.View callback;

    private Task task;

    public Presenter(SurveyInfoFile surveyInfoFile, Contract.View callback) {
        this.surveyInfoFile = surveyInfoFile;
        this.callback = callback;
    }

    @Override
    public void fetchListOfSurveys() {
        if (task != null) {
            task.cancel(true);
        }

        task = new Task(surveyInfoFile, callback);
        task.execute();
    }

    private static class Task extends AsyncTask<Void, Void, List<SurveyListData>> {
        private final SurveyInfoFile surveyInfoFile;
        private final Contract.View callback;

        private final ExecutorService pool = Executors.newSingleThreadExecutor();

        public Task(SurveyInfoFile surveyInfoFile, Contract.View callback) {
            this.surveyInfoFile = surveyInfoFile;
            this.callback = callback;
        }

        @Override
        protected List<SurveyListData> doInBackground(Void... params) {
            try {

                SavedSurveyInfoFileData savedSurveyInfoFileData = pool.submit(surveyInfoFile.getInfoJsonParsed()).get();

                List<Data> surveyInfoFileDataList = savedSurveyInfoFileData.getSurveyData();

                ArrayList<SurveyListData> surveyListDataArrayList = new ArrayList<>();

                if (surveyInfoFileDataList != null) {

                    for (Data surveyData : surveyInfoFileDataList) {
                        surveyListDataArrayList.add(SurveyListData.adapter(surveyData.get_id()
                                , surveyData.getSurveyName()));
                    }
                }

                return surveyListDataArrayList;

            } catch (ExecutionException e) {
                Timber.e(e.getMessage());
            } catch (InterruptedException e) {
                Timber.e(e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<SurveyListData> surveyListDataList) {
            callback.hideLoading();

            if (surveyListDataList == null) {
                callback.onError(R.string.cannot_get_data);
            } else {
                callback.onSurveysFetched(surveyListDataList);
            }
        }
    }
}
