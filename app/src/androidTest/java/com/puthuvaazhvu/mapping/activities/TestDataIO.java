package com.puthuvaazhvu.mapping.activities;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.SurveyUtils;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.saving.AnswerIOUtils;
import com.puthuvaazhvu.mapping.utils.saving.SurveyIOUtils;
import com.puthuvaazhvu.mapping.utils.saving.modals.AnswersInfo;
import com.puthuvaazhvu.mapping.utils.saving.modals.SurveyInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;


/**
 * Created by muthuveerappans on 01/02/18.
 */

@RunWith(AndroidJUnit4.class)
public class TestDataIO {

    private SurveyIOUtils surveyIOUtils;
    private AnswerIOUtils answerIOUtils;

    @Before
    public void setUp() {
        surveyIOUtils = SurveyIOUtils.getInstance();
        answerIOUtils = AnswerIOUtils.getInstance();
    }

    @Test
    public void testAnswerSave() throws IOException {
        String a = Utils.readFromInputStream(InstrumentationRegistry.getContext().getAssets()
                .open("answers_data_1.json"));

        JsonParser jsonParser = new JsonParser();

        Survey survey = SurveyUtils.getSurveyWithUpdatedAnswers(jsonParser.parse(a).getAsJsonObject()).blockingFirst();

        Question question = survey.getRootQuestion().getAnswers().get(0).getChildren().get(1)
                .getAnswers().get(0).getChildren().get(0);
        assertThat(question.getRawNumber(), is("2.0"));

        AnswersInfo answersInfo = answerIOUtils.saveAnswerToFile(survey, question, false).blockingFirst();

        assertThat(answersInfo.getSurveys().size(), is(greaterThan(0)));
        assertThat(answersInfo.getVersion(), is(Constants.Versions.ANSWERS_INFO_VERSION));
        assertThat(answersInfo.getSurvey("5a08957bad04f82a15a0e974").getLatestLoggedSnapshot().getPathToLastQuestion(), is("0,0,1,0,0"));
    }

    @Test
    public void testSaveSurvey() throws IOException {

        String a = Utils.readFromInputStream(InstrumentationRegistry.getContext().getAssets()
                .open("survey_data_1.json"));
        SurveyInfo surveyInfo = surveyIOUtils.saveSurvey(a).blockingFirst();

        assertThat(surveyInfo.getSurveys().size(), is(greaterThan(1)));
        assertThat(surveyInfo.getVersion(), is(Constants.Versions.SURVEY_INFO_VERSION));
        assertThat(surveyInfo.getSurvey("5a08957bad04f82a15a0e974"), is(notNullValue()));

        surveyInfo = surveyIOUtils.readSurveysInfoFile().blockingFirst();

        assertThat(surveyInfo, is(notNullValue()));
    }
}
