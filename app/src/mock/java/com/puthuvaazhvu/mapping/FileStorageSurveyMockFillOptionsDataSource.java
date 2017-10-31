package com.puthuvaazhvu.mapping;

import android.content.Context;
import android.os.Handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.data.DataSource;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class FileStorageSurveyMockFillOptionsDataSource implements DataSource<Survey> {
    private final GetFromFile getFromFile;
    private final Handler handler;
    private final ExecutorService pool = Executors.newSingleThreadExecutor();
    private final Context context;

    public FileStorageSurveyMockFillOptionsDataSource(Handler handler, Context context) {
        this.getFromFile = GetFromFile.getInstance();
        this.handler = handler;
        this.context = context;
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

                            Survey survey = new Survey(surveyJsonObject);

                            // fill with dynamic options
                            final Survey surveyUpdated = fillWithDynamicOptions(survey, context);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onLoaded(surveyUpdated);
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

    private static Survey fillWithDynamicOptions(Survey survey, Context context) {
        String optionsFillJson = Utils.readFromAssetsFile(context, "options_fill.json");

        JsonParser jsonParser = new JsonParser();
        JsonObject rootJson = jsonParser.parse(optionsFillJson).getAsJsonObject();

        // iterate over the elements
        for (Map.Entry<String, JsonElement> entry : rootJson.entrySet()) {
            String fillTag = entry.getKey();
            JsonArray optionsArray = entry.getValue().getAsJsonArray();

            ArrayList<Option> options = new ArrayList<>();

            for (JsonElement optionElement : optionsArray) {
                options.add(new Option(optionElement.getAsJsonObject()));
            }

            boolean result = survey.dynamicOptionsFillForQuestion(fillTag, options);

            if (!result) {
                Timber.e("Dynamic fill options could'nt be added at " + fillTag);
            }
        }

        return survey;
    }

    @Override
    public void saveData(Survey data) {
        throw new IllegalArgumentException("Not implemented");
    }
}
