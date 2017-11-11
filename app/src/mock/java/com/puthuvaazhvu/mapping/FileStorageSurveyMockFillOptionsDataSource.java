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

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
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
        File file = new File(selection);
        if (file.exists()) {
            getFromFile.execute(file)
                    .map(new Function<String, Survey>() {
                        @Override
                        public Survey apply(@NonNull String data) throws Exception {
                            JsonParser parser = new JsonParser();
                            JsonObject surveyJsonObject = parser.parse(data).getAsJsonObject();

                            Survey survey = new Survey(surveyJsonObject);

                            // fill with dynamic options
                            return fillWithDynamicOptions(survey, context);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Survey>() {
                                   @Override
                                   public void accept(@NonNull Survey survey) throws Exception {
                                       callback.onLoaded(survey);
                                   }
                               },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception {
                                    callback.onError(throwable.getLocalizedMessage());
                                }
                            });
        }
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
