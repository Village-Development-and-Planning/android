package com.puthuvaazhvu.mapping;

import android.content.Context;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
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
        Survey survey = TestUtils.getSurvey(this);

        Question root = survey.getQuestion();

        assertThat(root.getParent(), is(nullValue()));

        Question surveyorCodeQuestion = root.getChildren().get(0);
        assertThat(surveyorCodeQuestion.getNumber(), is("1"));
        assertThat(surveyorCodeQuestion.getChildren().size(), is(4));

        Question habitationQuestion = root.getChildren().get(1);
        assertThat(habitationQuestion.getNumber(), is("2"));
        assertThat(habitationQuestion.getChildren().size(), is(3));

        Question roadQuestion = habitationQuestion.getChildren().get(1);
        assertThat(roadQuestion.getNumber(), is("2.1"));
        assertThat(roadQuestion.getParent().getNumber(), is("2"));
        assertThat(roadQuestion.getChildren().size(), is(7));

        Question squareQuestion = habitationQuestion.getChildren().get(2);
        assertThat(squareQuestion.getNumber(), is("2.2"));
        assertThat(squareQuestion.getParent().getNumber(), is("2"));
        assertThat(squareQuestion.getChildren().size(), is(2));

        assertThat(squareQuestion.getChildren().get(1).getChildren().get(1).getChildren().get(0).getNumber(), is("2.2.3.1"));
        assertThat(squareQuestion
                        .getChildren().get(1)
                        .getChildren().get(1)
                        .getChildren().get(0)
                        .getParent()
                        .getParent()
                        .getParent().getNumber()
                , is("2.2"));

        // pre flow test
        Question facilitiesInRoadQuestion = roadQuestion.getChildren().get(6);
        assertThat(facilitiesInRoadQuestion.getNumber(), is("2.1.7"));
        assertThat(facilitiesInRoadQuestion.getChildren()
                .get(1).getChildren().get(0).getFlowPattern().getPreFlow().getQuestionNumber(), is("2.1.7.4"));

    }
}
