package com.puthuvaazhvu.mapping;

import android.os.Handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.data.DataSource;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class FileStorageSurveyDataSource implements DataSource<Survey> {
    private final GetFromFile getFromFile;
    private final Handler handler;
    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    public FileStorageSurveyDataSource(Handler handler) {
        this.getFromFile = GetFromFile.getInstance();
        this.handler = handler;
    }

    @Override
    public void getAllData(DataSourceCallback<ArrayList<Survey>> callback) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void getData(final String selection, final DataSourceCallback<Survey> callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(selection);

                try {
                    if (file.exists()) {
                        String surveyJsonString = pool.submit(getFromFile.execute(file)).get();

                        if (surveyJsonString != null) {
                            JsonParser parser = new JsonParser();
                            JsonObject surveyJsonObject = parser.parse(surveyJsonString).getAsJsonObject();

                            final Survey survey = new Survey(surveyJsonObject);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onLoaded(survey);
                                }
                            });

                            return;
                        }
                    }

                } catch (InterruptedException e) {
                    Timber.e("Error occurred while loading survey from file " + e.getMessage());
                } catch (ExecutionException e) {
                    Timber.e("Error occurred while loading survey from file " + e.getMessage());
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError("Error occurred while loading survey from file");
                    }
                });

            }
        }).start();
    }

    @Override
    public void saveData(Survey data) {
        throw new IllegalArgumentException("Not implemented");
    }
}
