package com.puthuvaazhvu.mapping;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.InputStream;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public class TestUtils {
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

        return QuestionUtils.populateQuestionFromJson(null, questionJson);
    }

    public static JsonObject getAnswersJson(Object obj) {
        return getJson(obj, "answers_data_1.json");
    }

    public static JsonObject getJson(Object obj, String fileName) {
        String jsonString = Utils.readFromInputStream(getDataFormFile(obj, fileName));

        assertThat(jsonString, notNullValue());

        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(jsonString).getAsJsonObject();
    }

    public static ArrayList<Option> getDummyOptions() {
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("", "dummy", null, "", "-1"));
        return options;
    }

    public static Answer getDummyAnswer(Question question) {
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("", "", null, "", "-1"));
        return new Answer(options, question);
    }

    private static InputStream getDataFormFile(Object obj, String fileName) {
        return obj.getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
