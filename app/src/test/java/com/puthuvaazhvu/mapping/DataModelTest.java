package com.puthuvaazhvu.mapping;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;

/**
 * Created by muthuveerappans on 9/28/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class DataModelTest {

    @Mock
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSurveyModel() {
        String fileName = "survey_4.json";
        String surveyDataString = Utils.readFromInputStream(getDataFormFile(this, fileName));

        assertThat(surveyDataString, notNullValue());

        assertThat(surveyDataString, containsString("_id"));

        JsonParser jsonParser = new JsonParser();
        JsonObject surveyJson = jsonParser.parse(surveyDataString).getAsJsonObject();

        assertThat(surveyJson, notNullValue());

        Survey survey = new Survey(surveyJson);

        assertThat(survey, is(notNullValue()));

        // check the first branch of children has the correct size.
        assertThat(survey.getQuestionList().size(), equalTo(6));

        Question habitationQuestion = survey.getQuestionList().get(5);
        assertThat(habitationQuestion.getRawNumber(), is("2"));
        assertThat(habitationQuestion.getChildren().get(1).getRawNumber(), is("2.2"));
        assertThat(habitationQuestion.getChildren().get(1).getChildren().get(2).getRawNumber(), is("2.2.3"));
        assertThat(habitationQuestion.getChildren().get(1).getChildren().get(2).getChildren().get(0)
                .getRawNumber(), is("2.2.3.1"));
        assertThat(habitationQuestion.getChildren().get(1).getChildren().get(2).getChildren().get(0).getParent()
                .getRawNumber(), is("2.2.3"));
        assertThat(habitationQuestion.getChildren().get(5).getText().getEnglish(), is("Tag a PDS outlet"));
        assertThat(habitationQuestion.getChildren().get(8).getChildren().get(0).getRawNumber(), is("2.9.1"));
        assertThat(habitationQuestion.getChildren().get(8).getChildren().get(0).getOptionList().size(), equalTo(12));

        assertThat(habitationQuestion.getChildren().get(17).getChildren().get(2).getChildren().get(2)
                .getRawNumber(), equalTo("2.18.3.3"));
        assertThat(habitationQuestion.getChildren().get(17).getChildren().get(2).getChildren().get(2).getFlowPattern().getChildFlow().getMode(), equalTo(ChildFlow.Modes.CASCADE));
    }

    private static InputStream getDataFormFile(Object obj, String fileName) {
        return obj.getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
