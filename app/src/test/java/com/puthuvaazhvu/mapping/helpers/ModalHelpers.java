package com.puthuvaazhvu.mapping.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.InputStream;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public class ModalHelpers {
    public static Survey getSurvey(Object obj) {
        String fileName = "survey_data_test_unit.json";
        String surveyDataString = Utils.readFromInputStream(getDataFormFile(obj, fileName));

        assertThat(surveyDataString, notNullValue());

        assertThat(surveyDataString, containsString("_id"));

        JsonParser jsonParser = new JsonParser();
        JsonObject surveyJson = jsonParser.parse(surveyDataString).getAsJsonObject();

        assertThat(surveyJson, notNullValue());

        return new Survey(surveyJson);
    }

    public static Question getMessageQuestion(Object obj) {
        String fileName = "message_question.json";
        String questionString = Utils.readFromInputStream(getDataFormFile(obj, fileName));

        assertThat(questionString, notNullValue());

        JsonParser jsonParser = new JsonParser();
        JsonObject questionJson = jsonParser.parse(questionString).getAsJsonObject();

        assertThat(questionJson, notNullValue());

        return Question.populateQuestion(questionJson);
    }

    private static InputStream getDataFormFile(Object obj, String fileName) {
        return obj.getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
