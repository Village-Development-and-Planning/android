package com.puthuvaazhvu.mapping.activities;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.filestorage.AnswerIO;
import com.puthuvaazhvu.mapping.filestorage.DataInfoIO;
import com.puthuvaazhvu.mapping.filestorage.SnapshotIO;
import com.puthuvaazhvu.mapping.filestorage.SurveyIO;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by muthuveerappans on 17/02/18.
 */

@RunWith(AndroidJUnit4.class)
public class TestIO {

    @Test
    public void survey_test() throws Exception {
        resetDataInfo();

        Survey survey = getSurvey("answers_data_1.json");

        SurveyIO surveyIO = new SurveyIO("5a08957bad04f82a15a0e974", "TEST");
        File file = surveyIO.save(survey).blockingFirst();

        assertThat(file.getName(), is("5a08957bad04f82a15a0e974.bytes"));

        DataInfo dataInfo = getDataInfo();

        assertThat(dataInfo.getSurveysInfo().getSurveys().size(), is(1));
        assertThat(dataInfo.getSurveysInfo().getSurvey("5a08957bad04f82a15a0e974").getSurveyName(), is("TEST"));

        file = surveyIO.save(survey).blockingFirst();
        assertThat(dataInfo.getSurveysInfo().getSurveys().size(), is(1));

        surveyIO = new SurveyIO("5a08957bad04f82a15a0e975", "test");
        file = surveyIO.save(survey).blockingFirst();

        assertThat(file.getName(), is("5a08957bad04f82a15a0e975.bytes"));

        dataInfo = getDataInfo();

        assertThat(dataInfo.getSurveysInfo().getSurveys().size(), is(2));
        assertThat(dataInfo.getSurveysInfo().getSurvey("5a08957bad04f82a15a0e975").getSurveyName(), is("test"));
    }

    @Test
    public void answer_test() throws Exception {
        resetDataInfo();

        Survey survey = getSurvey("answers_data_1.json");

        AnswerIO answerIO = new AnswerIO("5a08957bad04f82a15a0e974",
                "TEST", "5a08957bad04f82a15a0e974_1");
        File file = answerIO.save(survey).blockingFirst();

        assertThat(file.getName(), is("5a08957bad04f82a15a0e974_1.json"));

        DataInfo dataInfo = getDataInfo();

        assertThat(dataInfo.getAnswersInfo().getAnswersCount("5a08957bad04f82a15a0e974"), is(1));
        assertThat(dataInfo.getAnswersInfo().getAnswer("5a08957bad04f82a15a0e974_1").getSurveyName(), is("TEST"));

        answerIO = new AnswerIO("5a08957bad04f82a15a0e974",
                "TEST", "5a08957bad04f82a15a0e974_2");
        file = answerIO.save(getSurvey("answers_data_1.json")).blockingFirst();

        assertThat(file.getName(), is("5a08957bad04f82a15a0e974_2.json"));

        dataInfo = getDataInfo();

        assertThat(dataInfo.getAnswersInfo().getAnswersCount("5a08957bad04f82a15a0e974"), is(2));

        answerIO = new AnswerIO("5a08957bad04f82a15a0e974",
                "TEST", "5a08957bad04f82a15a0e974_1");
        survey = answerIO.read().blockingFirst();

        assertThat(survey.getId(), is("5a08957bad04f82a15a0e974"));

        answerIO = new AnswerIO("5a08957bad04f82a15a0e974",
                "TEST", "5a08957bad04f82a15a0e974_2");
        answerIO.delete().blockingFirst();

        dataInfo = getDataInfo();
        assertThat(dataInfo.getAnswersInfo().getAnswer("5a08957bad04f82a15a0e974"), is(nullValue()));
        assertThat(dataInfo.getAnswersInfo().getAnswersCount("5a08957bad04f82a15a0e974"), is(1));
    }

    @Test
    public void snapshot_test() throws Exception {
        resetDataInfo();

        SnapshotIO snapshotIO = new SnapshotIO("N/A", "5a08957bad04f82a15a0e974_0"
                , "5a08957bad04f82a15a0e974", "TEST");
        File file = snapshotIO.save(getSurvey("answers_data_1.json")).blockingFirst();
        assertThat(file.exists(), is(true));
        assertThat(file.getName(), is("5a08957bad04f82a15a0e974_0.bytes"));

        DataInfo dataInfo = getDataInfo();

        assertThat(dataInfo.getSnapshotsInfo().getSurvey("5a08957bad04f82a15a0e974").getSnapshots().size()
                , is(1));

        assertThat(dataInfo.getSnapshotsInfo().getSurvey("5a08957bad04f82a15a0e974")
                        .getLatestLoggedSnapshot().getSnapshotID()
                , is("5a08957bad04f82a15a0e974_0"));

        assertThat(dataInfo.getSnapshotsInfo().getSurvey("5a08957bad04f82a15a0e974")
                        .getLatestLoggedSnapshot().getPathToLastQuestion()
                , is("N/A"));

        // check for saving second snapshot
        snapshotIO = new SnapshotIO("N/A", "5a08957bad04f82a15a0e974_1"
                , "5a08957bad04f82a15a0e974", "TEST");
        snapshotIO.save(getSurvey("answers_data_1.json")).blockingFirst();

        dataInfo = getDataInfo();

        assertThat(dataInfo.getSnapshotsInfo().getSurvey("5a08957bad04f82a15a0e974").getSnapshots().size()
                , is(1));

        // read
        SnapshotIO io = new SnapshotIO("5a08957bad04f82a15a0e974_1");
        Survey survey = io.read().blockingFirst();
        assertThat(survey.getId(), is("5a08957bad04f82a15a0e974"));
    }

    private void resetDataInfo() {
        DataInfoIO dataInfoIO = new DataInfoIO();
        if (new File(dataInfoIO.getAbsolutePath()).exists())
            dataInfoIO.delete().blockingFirst();
    }

    private DataInfo getDataInfo() {
        DataInfoIO dataInfoIO = new DataInfoIO();
        DataInfo dataInfo = dataInfoIO.read().blockingFirst();
        return dataInfo;
    }

    private Survey getSurvey(String file) {
        try {
            String a = Utils.readFromInputStream(InstrumentationRegistry.getContext().getAssets()
                    .open(file));
            JsonParser jsonParser = new JsonParser();
            return new Survey(jsonParser.parse(a).getAsJsonObject());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
