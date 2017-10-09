package com.puthuvaazhvu.mapping.modals;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.utils.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by muthuveerappans on 10/6/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class SurveyDataModelTest {
    @Mock
    private Context context;

    public Survey survey;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSurveyModel() {
        String fileName = "survey_6.json";
        String surveyDataString = Utils.readFromInputStream(getDataFormFile(this, fileName));

        assertThat(surveyDataString, notNullValue());

        assertThat(surveyDataString, containsString("_id"));

        JsonParser jsonParser = new JsonParser();
        JsonObject surveyJson = jsonParser.parse(surveyDataString).getAsJsonObject();

        assertThat(surveyJson, notNullValue());

        survey = new Survey(surveyJson);

        assertThat(survey, is(notNullValue()));
    }

    private static InputStream getDataFormFile(Object obj, String fileName) {
        return obj.getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
