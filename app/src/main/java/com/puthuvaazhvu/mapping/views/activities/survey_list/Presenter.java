package com.puthuvaazhvu.mapping.views.activities.survey_list;

import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.JsonHelper;
import com.puthuvaazhvu.mapping.utils.info_file.UpdateInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.InfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SavedSurveyInfoFile;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;

import java.io.File;
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
    private final UpdateInfoFile updateInfoFile;
    private final Contract.View callback;

    private Task task;

    public Presenter(UpdateInfoFile updateInfoFile, Contract.View callback) {
        this.updateInfoFile = updateInfoFile;
        this.callback = callback;
    }

    @Override
    public void fetchListOfSurveys() {
        if (task != null) {
            task.cancel(true);
        }

        task = new Task(updateInfoFile, callback);
        task.execute();
    }

    private static class Task extends AsyncTask<Void, Void, List<String>> {
        private final UpdateInfoFile updateInfoFile;
        private final Contract.View callback;

        private final ExecutorService pool = Executors.newSingleThreadExecutor();

        public Task(UpdateInfoFile updateInfoFile, Contract.View callback) {
            this.updateInfoFile = updateInfoFile;
            this.callback = callback;
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {

                InfoFile infoFile = pool.submit(updateInfoFile.getInfoJsonParsed()).get();

                List<SavedSurveyInfoFile> savedSurveyInfoFileList = infoFile.getSavedSurveyInfoFileList();

                if (savedSurveyInfoFileList != null) {

                    ArrayList<String> surveyIds = new ArrayList<>();

                    for (SavedSurveyInfoFile savedSurveyInfoFile : savedSurveyInfoFileList) {
                        String surveyID = savedSurveyInfoFile.get_id();
                        surveyIds.add(surveyID);
                    }

                    return surveyIds;
                }

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
