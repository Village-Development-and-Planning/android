package com.puthuvaazhvu.mapping;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.Data.DataSource;
import com.puthuvaazhvu.mapping.Modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.io.IOException;
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
        String surveyDataString = readFromStream(getDataFormFile(this, fileName));

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

    private static String readFromStream(InputStream is) {
        String json = null;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.i(Constants.LOG_TAG, "Error reading the JSON file from assets. " + ex.getMessage());
        }
        return json;
    }
}