package com.puthuvaazhvu.mapping.modals.utils;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 30/01/18.
 */

public class SurveyUtils {

    public static Observable<Survey> getSurveyWithUpdatedAnswers(final JsonObject surveyJson) {
        return Observable.create(new ObservableOnSubscribe<Survey>() {
            @Override
            public void subscribe(ObservableEmitter<Survey> e) throws Exception {
                Survey survey = new Survey(surveyJson);
                JsonObject questionJson = JsonHelper.getJsonObject(surveyJson, "question");
                if (questionJson != null) {
                    QuestionUtils.populateAnswersFromJson(survey.getRootQuestion(), questionJson);
                }
                e.onNext(survey);
                e.onComplete();
            }
        });
    }
}
