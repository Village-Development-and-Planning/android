package com.puthuvaazhvu.mapping.data;

import android.content.SharedPreferences;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.SurveyAPIInfo;
import com.puthuvaazhvu.mapping.modals.utils.SurveyUtils;
import com.puthuvaazhvu.mapping.network.APIError;
import com.puthuvaazhvu.mapping.network.APIUtils;
import com.puthuvaazhvu.mapping.network.implementations.ListSurveysAPI;
import com.puthuvaazhvu.mapping.network.implementations.SingleSurveyAPI;
import com.puthuvaazhvu.mapping.utils.saving.AnswerIOUtils;
import com.puthuvaazhvu.mapping.utils.saving.SurveyIOUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 01/02/18.
 */

public class SurveyDataRepository {
    private static SurveyDataRepository surveyDataRepository;
    private final SingleSurveyAPI singleSurveyAPI;
    private final ListSurveysAPI listSurveysAPI;
    private final AnswerIOUtils answerIOUtils;
    private final SurveyIOUtils surveyIOUtils;

    public static SurveyDataRepository getInstance(SharedPreferences sharedPreferences) {
        if (surveyDataRepository == null) {
            surveyDataRepository = new SurveyDataRepository(sharedPreferences);
        }
        return surveyDataRepository;
    }

    private SurveyDataRepository(SharedPreferences sharedPreferences) {
        singleSurveyAPI = SingleSurveyAPI.getInstance(APIUtils.getAuth(sharedPreferences));
        surveyIOUtils = SurveyIOUtils.getInstance();
        listSurveysAPI = ListSurveysAPI.getInstance(APIUtils.getAuth(sharedPreferences));
        answerIOUtils = AnswerIOUtils.getInstance();
    }

    public Observable<List<SurveyAPIInfo>> getSurveysFromAPI() {
        return Observable.create(new ObservableOnSubscribe<List<SurveyAPIInfo>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<SurveyAPIInfo>> emitter) throws Exception {
                listSurveysAPI.getSurveysList(new ListSurveysAPI.ListSurveysAPICallbacks() {
                    @Override
                    public void onSurveysLoaded(List<SurveyAPIInfo> surveyInfoList) {
                        emitter.onNext(surveyInfoList);
                        emitter.onComplete();
                    }

                    @Override
                    public void onErrorOccurred(APIError error) {
                        emitter.onError(new Throwable(error.message()));
                    }
                });
            }
        });
    }

    public Observable<Survey> getSurveyFromAPI(String surveyID) {
        return singleSurveyAPI.getSurvey(surveyID)
                .map(new Function<String, Survey>() {
                    @Override
                    public Survey apply(@NonNull String s) throws Exception {
                        JsonParser parser = new JsonParser();
                        JsonObject surveyJsonObject = parser.parse(s).getAsJsonObject();

                        return new Survey(surveyJsonObject);
                    }
                });
    }

    public Observable<Survey> getSurveyFromFile(String id, String snapshotPath, String snapShotFileName) {
        if (snapshotPath != null) {
            // read from answers dir
            return answerIOUtils.getAnswerFromFile(snapShotFileName)
                    .flatMap(new Function<String, ObservableSource<Survey>>() {
                        @Override
                        public ObservableSource<Survey> apply(String s) throws Exception {
                            JsonParser jsonParser = new JsonParser();
                            JsonElement jsonElement = jsonParser.parse(s);

                            if (jsonElement != null)
                                return SurveyUtils.getSurveyWithUpdatedAnswers(jsonElement.getAsJsonObject());
                            else
                                throw new IllegalArgumentException("Error parsing the survey json.");
                        }
                    });

        } else {

            // read from the survey dir
            return surveyIOUtils.getSurveyFromFile(id)
                    .map(new Function<String, Survey>() {
                        @Override
                        public Survey apply(String s) throws Exception {
                            JsonParser jsonParser = new JsonParser();
                            JsonElement jsonElement = jsonParser.parse(s);

                            if (jsonElement != null)
                                return new Survey(jsonElement.getAsJsonObject());
                            else
                                throw new IllegalArgumentException("Error parsing the survey json.");
                        }
                    });
        }
    }

}
