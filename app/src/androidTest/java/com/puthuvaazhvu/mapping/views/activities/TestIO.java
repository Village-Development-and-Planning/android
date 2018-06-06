package com.puthuvaazhvu.mapping.views.activities;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.filestorage.io.AnswerIO;
import com.puthuvaazhvu.mapping.filestorage.io.DataInfoIO;
import com.puthuvaazhvu.mapping.filestorage.io.SnapshotIO;
import com.puthuvaazhvu.mapping.filestorage.io.SurveyIO;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.deserialization.SurveyGsonAdapter;
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
    public void answersIO() throws Exception {
        resetDataInfo();

        Survey survey = getSurvey("survey_household.json");

        AnswerIO answerIO = new AnswerIO("123",
                "TEST", "123_1");
        File file = answerIO.save(survey).blockingFirst();

        assertThat("Check the file name", file.getName(), is("123_1.json"));

        DataInfo dataInfo = getDataInfo();

        assertThat(
                "Check the count of data info - 1",
                dataInfo.getAnswersInfo().getAnswersCount("123"),
                is(1)
        );
        assertThat(dataInfo.getAnswersInfo().getAnswer("123_1").getSurveyName(), is("TEST"));

        answerIO = new AnswerIO(
                "123",
                "TEST",
                "123_2"
        );
        file = answerIO.save(getSurvey("survey_household.json")).blockingFirst();

        assertThat(file.getName(), is("123_2.json"));

        dataInfo = getDataInfo();

        assertThat(
                "Check if the answers count has been updated in data info",
                dataInfo.getAnswersInfo().getAnswersCount("123"),
                is(2)
        );
    }


    @Test
    public void surveyIO() throws Exception {
        resetDataInfo();

        Survey survey = getSurvey("survey_household.json");

        SurveyIO surveyIO = new SurveyIO("123", "TEST");
        File file = surveyIO.save(survey).blockingFirst();

        assertThat(
                "Check File name same as surveyID",
                file.getName(),
                is("123.bytes")
        );

        DataInfo dataInfo = getDataInfo();

        assertThat(
                "Survey size in data info should be 1",
                dataInfo.getSurveysInfo().getSurveys().size(),
                is(1)
        );
        assertThat(
                "Test if survey can be read",
                dataInfo.getSurveysInfo().getSurvey("123").getSurveyName(),
                is("TEST")
        );

        surveyIO.save(survey).blockingFirst();
        assertThat(
                "Test for no duplication",
                dataInfo.getSurveysInfo().getSurveys().size(),
                is(1)
        );

        surveyIO = new SurveyIO("abc", "test");
        file = surveyIO.save(survey).blockingFirst();

        dataInfo = getDataInfo();

        assertThat(
                "Survey size in data info should be updated to 2",
                dataInfo.getSurveysInfo().getSurveys().size(),
                is(2)
        );
    }

    @Test
    public void snapshot_test() throws Exception {
        resetDataInfo();

        SnapshotIO snapshotIO = new SnapshotIO("000", "123_0"
                , "123", "TEST");
        File file = snapshotIO.save(getSurvey("survey_household.json")).blockingFirst();

        assertThat("Check if the file is created", file.exists(), is(true));
        assertThat("Check file name is same as snapshot id", file.getName(), is("123_0.bytes"));

        DataInfo dataInfo = getDataInfo();

        assertThat(
                "Check snapshot count of survey 123",
                dataInfo.getSnapshotsInfo().getSurvey("123").getSnapshots().size()
                , is(1)
        );

        assertThat(
                "Check latest snapshotID",
                dataInfo.getSnapshotsInfo().getSurvey("123")
                        .getLatestLoggedSnapshot().getSnapshotID()
                , is("123_0"));

        assertThat(
                "Check snapshot path to question",
                dataInfo.getSnapshotsInfo().getSurvey("123")
                        .getLatestLoggedSnapshot().getPathToLastQuestion()
                , is("000"));

        // check for saving second snapshot
        snapshotIO = new SnapshotIO("N/A", "123_1"
                , "123", "TEST");
        snapshotIO.save(getSurvey("survey_household.json")).blockingFirst();

        dataInfo = getDataInfo();

        assertThat(
                "Make sure the survey count has not changed",
                dataInfo.getSnapshotsInfo().getSurvey("123").getSnapshots().size(),
                is(1)
        );

        // read file IO
        SnapshotIO io = new SnapshotIO("123_1");
        Survey survey = io.read().blockingFirst();
        assertThat("Check if survey 123 is read properly", survey.getQuestion().getType(), is("ROOT"));
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
            final GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());
            gsonBuilder.setPrettyPrinting();

            final Gson gson = gsonBuilder.create();

            JsonParser jsonParser = new JsonParser();
            return gson.fromJson(jsonParser.parse(a).getAsJsonObject(), Survey.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
