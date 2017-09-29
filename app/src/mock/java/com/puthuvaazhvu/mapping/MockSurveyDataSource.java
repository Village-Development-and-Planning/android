package com.puthuvaazhvu.mapping;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.data.DataSource;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.InputStream;
import java.util.ArrayList;

public class MockSurveyDataSource implements DataSource<Survey> {

    public MockSurveyDataSource() {
    }

    @Override
    public void getAllData(DataSourceCallback<ArrayList<Survey>> callback) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void getData(String selection, DataSourceCallback<Survey> callback) {
        String fileName = "survey_4.json";
        String surveyDataString = Utils.readFromInputStream(getDataFormFile(this, fileName));

        JsonParser jsonParser = new JsonParser();
        JsonElement surveyJsonElement = jsonParser.parse(surveyDataString);
        JsonObject surveyJsonObject = surveyJsonElement.getAsJsonObject();

        Survey survey = new Survey(surveyJsonObject);
        callback.onLoaded(survey);
    }

    @Override
    public void saveData(Survey data) {
        throw new IllegalArgumentException("Not implemented");
    }

    private static InputStream getDataFormFile(Object obj, String fileName) {
        return obj.getClass().getClassLoader().getResourceAsStream(fileName);
    }
}