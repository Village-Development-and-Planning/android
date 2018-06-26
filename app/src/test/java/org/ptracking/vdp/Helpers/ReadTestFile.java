package org.ptracking.vdp.Helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ptracking.vdp.modals.Survey;
import org.ptracking.vdp.modals.deserialization.SurveyGsonAdapter;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by muthuveerappans on 20/02/18.
 */

public class ReadTestFile {

    public static Survey getTestHouseholdSurvey(Object obj) {
        return getTestSurvey(obj, "survey_household.json");
    }

    public static Survey getTestMappingSurvey(Object obj) {
        return getTestSurvey(obj, "survey_mapping.json");
    }

    public static Survey getTestSurvey(Object obj, String filename) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());
        gsonBuilder.setPrettyPrinting();

        Gson gson = gsonBuilder.create();

        return gson.fromJson(
                ReadTestFile.readFromFileAsJson(obj, filename),
                Survey.class
        );
    }

    public static JsonObject readFromFileAsJson(Object obj, String fileName) {
        String jsonString = readTestFileAsString(obj, fileName);
        assertThat(jsonString, notNullValue());
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(jsonString).getAsJsonObject();
    }

    public static String readTestFileAsString(Object obj, String fileName) {
        InputStream is = obj.getClass().getClassLoader().getResourceAsStream(fileName);

        String json = null;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Timber.e("Error reading the file. " + ex.getMessage());
        }
        return json;
    }
}
