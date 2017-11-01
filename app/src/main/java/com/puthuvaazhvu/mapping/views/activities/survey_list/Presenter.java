package com.puthuvaazhvu.mapping.views.activities.survey_list;

import android.os.AsyncTask;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.info_file.SurveyInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SavedSurveyInfoFileData;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SurveyInfoFileData;

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

    private static class Task extends AsyncTask<Void, Void, List<String>> {
        private final SurveyInfoFile surveyInfoFile;
        private final Contract.View callback;

        private final ExecutorService pool = Executors.newSingleThreadExecutor();

        public Task(SurveyInfoFile surveyInfoFile, Contract.View callback) {
            this.surveyInfoFile = surveyInfoFile;
            this.callback = callback;
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {

                SavedSurveyInfoFileData savedSurveyInfoFileData = pool.submit(surveyInfoFile.getInfoJsonParsed()).get();

                List<SurveyInfoFileData> surveyInfoFileDataList = savedSurveyInfoFileData.getSurveyInfoFileDataList();

                ArrayList<String> surveyIds = new ArrayList<>();

                if (surveyInfoFileDataList != null) {

                    for (SurveyInfoFileData surveyInfoFileData : surveyInfoFileDataList) {
                        String surveyID = surveyInfoFileData.get_id();
                        surveyIds.add(surveyID);
                    }
                }

                return surveyIds;

            } catch (ExecutionException e) {
                Timber.e(e.getMessage());
            } catch (InterruptedException e) {
                Timber.e(e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<String> surveyIDs) {
            callback.hideLoading();

            if (surveyIDs == null) {
                callback.onError(R.string.cannot_get_data);
            } else {
                callback.onSurveysFetched(SurveyListData.adapter(surveyIDs));
            }
        }
    }
}
