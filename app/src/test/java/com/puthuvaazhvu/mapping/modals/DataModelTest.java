package com.puthuvaazhvu.mapping.modals;

import android.content.Context;

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

    private SurveyDataModelTest surveyDataModelTest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        surveyDataModelTest = new SurveyDataModelTest();
    }

    @Test
    public void testSurveyModel() {
        surveyDataModelTest.testSurveyModel();

        Survey survey = surveyDataModelTest.survey;

        Question root = survey.getQuestionList().get(0);

        assertThat(root.getParent(), is(nullValue()));

        Question surveyorCodeQuestion = root.getChildren().get(0);
        assertThat(surveyorCodeQuestion.getRawNumber(), is("1"));
        assertThat(surveyorCodeQuestion.getChildren().size(), is(4));

        Question habitationQuestion = root.getChildren().get(1);
        assertThat(habitationQuestion.getRawNumber(), is("2"));
        assertThat(habitationQuestion.getChildren().size(), is(14));

        Question roadQuestion = habitationQuestion.getChildren().get(1);
        assertThat(roadQuestion.getRawNumber(), is("2.1"));
        assertThat(roadQuestion.getParent().getRawNumber(), is("2"));
        assertThat(roadQuestion.getChildren().size(), is(7));

        Question squareQuestion = habitationQuestion.getChildren().get(2);
        assertThat(squareQuestion.getRawNumber(), is("2.2"));
        assertThat(squareQuestion.getParent().getRawNumber(), is("2"));
        assertThat(squareQuestion.getChildren().size(), is(15));

        assertThat(squareQuestion.getChildren().get(3).getChildren().get(0).getRawNumber(), is("2.2.3.1"));
        assertThat(squareQuestion.getChildren().get(3).getChildren().get(0).getParent().getParent().getRawNumber(), is("2.2"));

        Question lastPointOfTheVillageCenterQuestion = habitationQuestion.getChildren().get(13);
        assertThat(lastPointOfTheVillageCenterQuestion.getRawNumber(), is("2.2.21"));
        assertThat(lastPointOfTheVillageCenterQuestion.getParent().getRawNumber(), is("2"));
        assertThat(lastPointOfTheVillageCenterQuestion.getChildren().size(), is(1));

        // pre flow test
        Question facilitiesInRoadQuestion = roadQuestion.getChildren().get(6);
        assertThat(facilitiesInRoadQuestion.getRawNumber(), is("2.1.7"));
        assertThat(facilitiesInRoadQuestion.getChildren()
                .get(1).getChildren().get(0).getFlowPattern().getPreFlow().getQuestionSkipRawNumber(), is("2.1.7.4"));

    }
}