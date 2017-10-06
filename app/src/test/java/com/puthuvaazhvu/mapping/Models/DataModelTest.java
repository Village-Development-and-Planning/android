package com.puthuvaazhvu.mapping.Models;

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
public class DataModelTest extends SurveyDataModelTest {

    @Mock
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Override
    @Test
    public void testSurveyModel() {
        super.testSurveyModel();

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
