package com.puthuvaazhvu.mapping.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileDataModal;

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
        return getSurvey(obj, fileName);
    }

    public static Survey getSurvey(Object obj, String fileName) {
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

    public static AnswersInfoFileDataModal getAnswersInfoFileModal(Object obj) {
        String fileName = "answers_info.json";
        String infoFileString = Utils.readFromInputStream(getDataFormFile(obj, fileName));

        assertThat(infoFileString, notNullValue());

        JsonParser jsonParser = new JsonParser();
        JsonObject infoJson = jsonParser.parse(infoFileString).getAsJsonObject();

        assertThat(infoJson, notNullValue());

        return new AnswersInfoFileDataModal(infoJson);
    }

    public static JsonObject getAnswersJson(Object obj) {
        return getJson(obj, "answers_data_1.json");
    }

    private static JsonObject getJson(Object obj, String fileName) {
        String jsonString = Utils.readFromInputStream(getDataFormFile(obj, fileName));

        assertThat(jsonString, notNullValue());

        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(jsonString).getAsJsonObject();
    }

    private static InputStream getDataFormFile(Object obj, String fileName) {
        return obj.getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
