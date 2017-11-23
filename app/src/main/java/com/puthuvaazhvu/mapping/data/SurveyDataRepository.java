package com.puthuvaazhvu.mapping.data;

import android.content.SharedPreferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.network.implementations.SingleSurveyAPI;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 11/23/17.
 */

public class SurveyDataRepository extends DataRepository {
    private static SurveyDataRepository surveyDataRepository;

    public static SurveyDataRepository getInstance(
            GetFromFile getFromFile,
            SharedPreferences sharedPreferences,
            SingleSurveyAPI singleSurveyAPI,
            String optionsFillJson
    ) {
        if (surveyDataRepository == null) {
            surveyDataRepository = new SurveyDataRepository(getFromFile, sharedPreferences, singleSurveyAPI, optionsFillJson);
        }
        return surveyDataRepository;
    }

    private final SingleSurveyAPI singleSurveyAPI;
    private final String optionsFillJson;

    private SurveyDataRepository(
            GetFromFile getFromFile,
            SharedPreferences sharedPreferences,
            SingleSurveyAPI singleSurveyAPI,
            String optionsFillJson
    ) {
        super(getFromFile, sharedPreferences);

        this.singleSurveyAPI = singleSurveyAPI;
        this.optionsFillJson = optionsFillJson;
    }

    public Single<Survey> getSurveyFromAPI(String surveyID) {
        return singleSurveyAPI.getSurvey(surveyID)
                .map(new Function<String, Survey>() {
                    @Override
                    public Survey apply(@NonNull String s) throws Exception {
                        JsonParser parser = new JsonParser();
                        JsonObject surveyJsonObject = parser.parse(s).getAsJsonObject();

                        return new Survey(surveyJsonObject);
                    }
                })
                .map(new Function<Survey, Survey>() {
                    @Override
                    public Survey apply(@NonNull Survey survey) throws Exception {
                        return fillWithDynamicOptions(survey, optionsFillJson);
                    }
                });
    }

    public Single<Survey> getSurveyFromFileAndUpdateWithAnswers(File answerFile) {

        return getFromFile.execute(answerFile)
                .flatMap(new Function<String, SingleSource<? extends Survey>>() {
                    @Override
                    public SingleSource<? extends Survey> apply(@NonNull String surveyJsonString) throws Exception {
                        JsonParser jsonParser = new JsonParser();
                        JsonObject surveyJsonWithAnswers = jsonParser.parse(surveyJsonString).getAsJsonObject();
                        Survey survey = new Survey(surveyJsonWithAnswers);
                        return Survey.getSurveyInstanceWithUpdatedAnswers(surveyJsonWithAnswers);
                    }
                })
                .map(new Function<Survey, Survey>() {
                    @Override
                    public Survey apply(@NonNull Survey survey) throws Exception {
                        return fillWithDynamicOptions(survey, optionsFillJson);
                    }
                });
    }

    public Single<Survey> getSurveyFromFile(File file) {
        return getDataFromFile(file)
                .map(new Function<String, Survey>() {
                         @Override
                         public Survey apply(@NonNull String s) throws Exception {
                             JsonParser parser = new JsonParser();
                             JsonObject surveyJsonObject = parser.parse(s).getAsJsonObject();

                             return new Survey(surveyJsonObject);
                         }
                     }
                )
                .map(new Function<Survey, Survey>() {
                    @Override
                    public Survey apply(@NonNull Survey survey) throws Exception {
                        return fillWithDynamicOptions(survey, optionsFillJson);
                    }
                });
    }

    private static Survey fillWithDynamicOptions(Survey survey, String optionsFillJson) {
        //String optionsFillJson = Utils.readFromAssetsFile(context, "options_fill.json");

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
}
