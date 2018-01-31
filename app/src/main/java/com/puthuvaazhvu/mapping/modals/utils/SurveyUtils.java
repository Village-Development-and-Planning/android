package com.puthuvaazhvu.mapping.modals.utils;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 30/01/18.
 */

public class SurveyUtils {

    public static Single<Survey> getSurveyWithUpdatedAnswers(final JsonObject surveyJson) {
        return Single.just(new Survey(surveyJson))
                .map(new Function<Survey, Survey>() {
                    @Override
                    public Survey apply(@NonNull Survey survey) throws Exception {
                        JsonObject questionJson = JsonHelper.getJsonObject(surveyJson, "question");
                        if (questionJson != null) {
                            QuestionUtils.populateAnswersFromJson(survey.getRootQuestion(), questionJson);
                        }
                        return survey;
                    }
                });
    }
}
