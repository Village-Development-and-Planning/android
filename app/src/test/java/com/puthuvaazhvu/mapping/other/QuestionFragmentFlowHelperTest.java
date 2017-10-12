package com.puthuvaazhvu.mapping.other;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.SurveyDataModelTest;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Flow.PreFlow;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.helpers.flow.QuestionFlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.flow.QuestionFlowHelperImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;

/**
 * Created by muthuveerappans on 10/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class QuestionFragmentFlowHelperTest {

    private SurveyDataModelTest surveyDataModelTest;
    private Survey survey;
    private QuestionFlowHelperImpl questionFlowHelper;
    private Question root;

    @Before
    public void setup() {
        surveyDataModelTest = new SurveyDataModelTest();

        surveyDataModelTest.testSurveyModel();
        survey = surveyDataModelTest.survey;
        root = survey.getQuestionList().get(0);
        questionFlowHelper = new QuestionFlowHelperImpl(root);
    }

    @Test
    public void test_shouldSkip_method() {
        String mockOption = "{\n" +
                "\"position\": \"1\",\n" +
                "\"option\": {\n" +
                "\"_id\": \"59d891bc7370f54913320606\",\n" +
                "\"type\": \"GENERIC\",\n" +
                "\"text\": {\n" +
                "\"english\": \"Yes\",\n" +
                "\"tamil\": \"ஆம்\",\n" +
                "\"_id\": \"59d891bc7370f54913320607\",\n" +
                "\"modifiedAt\": \"2017-10-07T08:35:08.409Z\"\n" +
                "},\n" +
                "\"__v\": 0,\n" +
                "\"modifiedAt\": \"2017-10-07T08:35:08.409Z\"\n" +
                "},\n" +
                "\"_id\": \"59d891bd7370f54913320fb0\"\n" +
                "}";
        JsonParser jsonParser = new JsonParser();
        JsonObject mockJson = jsonParser.parse(mockOption).getAsJsonObject();

        Question question_2_1_7 = root.getChildren().get(1).getChildren().get(1).getChildren().get(6);

        // add mock answer to question 2.1.7.4
        Question question_2_1_7_4 = question_2_1_7.getChildren().get(1);
        ArrayList<Option> mockOptions = new ArrayList<>();
        Option o = new Option(mockJson);
        assertThat(o.getId(), is("59d891bc7370f54913320606"));
        mockOptions.add(o);
        question_2_1_7_4.addAnswer(new Answer(mockOptions, question_2_1_7_4));

        Question question_2_1_7_4_2_ = question_2_1_7_4.getChildren().get(1);
        assertThat(question_2_1_7_4_2_.getRawNumber(), is("2.1.7.4.2"));
        PreFlow preFlow = question_2_1_7_4_2_.getFlowPattern().getPreFlow();

        boolean shouldSkip =
                questionFlowHelper.shouldSkip(preFlow.getQuestionSkipRawNumber(), preFlow.getOptionSkip(), question_2_1_7_4_2_);
        assertThat(shouldSkip, is(false));
    }

    @Test
    public void test_getQuestionsReverse_method() {
        Question q_2_1_7 =
                root.getChildren().get(1).getChildren().get(1).getChildren().get(6);

        Question q_2_1_7_3_4 = q_2_1_7.getChildren().get(0).getChildren().get(3);
        assertThat(q_2_1_7_3_4.getRawNumber(), is("2.1.7.3.4"));

        Question result = questionFlowHelper.getQuestionReverse("2.1.7", q_2_1_7_3_4);
        assertThat(result.getRawNumber(), is("2.1.7"));

        result = questionFlowHelper.getQuestionReverse("2.2", q_2_1_7_3_4);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testQuestionFlow() {
        ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);
        ArrayList<Question> removed = null;

        //                         --- start of testing ---

        testFlow(questionFlowHelper, new String[]{"1"});

        // move into the children
        testFlow(questionFlowHelper, new String[]{"1.2", "1.3", "1.4", "1.5"});

        // test the habitation question flow
        testFlow(questionFlowHelper, new String[]{"2"});

        // Road questions flow
        testFlow(questionFlowHelper, new String[]{"2.0", "2.1"});

        testFlow(questionFlowHelper, new String[]{"2.1.1", "2.1.2", "2.1.3", "2.1.4", "2.1.5", "2.1.6", "2.1.7"});

        Question question_2_1_7 = root.getChildren().get(1).getChildren().get(1).getChildren().get(6);

        // add mock answer to question 2.1.7.4
        String mockOption = "{\n" +
                "\"position\": \"0\",\n" +
                "\"option\": {\n" +
                "\"_id\": \"59d94d5ff5d04d1d3e12ee7b\",\n" +
                "\"type\": \"GENERIC\",\n" +
                "\"text\": {\n" +
                "\"english\": \"No\",\n" +
                "\"tamil\": \"இல்லை\",\n" +
                "\"_id\": \"59d94d5ff5d04d1d3e12ee7c\",\n" +
                "\"modifiedAt\": \"2017-10-07T21:55:43.536Z\"\n" +
                "},\n" +
                "\"__v\": 0,\n" +
                "\"modifiedAt\": \"2017-10-07T21:55:43.536Z\"\n" +
                "},\n" +
                "\"_id\": \"59d94d61f5d04d1d3e12f89c\"\n" +
                "}";
        // add the options
        Question question_2_1_7_4 = question_2_1_7.getChildren().get(1);
        addMockAnswerToQuestion(mockOption, "59d94d5ff5d04d1d3e12ee7b", question_2_1_7_4);

        // next question
        Question question_2_1_7_4_1 = question_2_1_7_4.getChildren().get(0);
        mockOption = "{\n" +
                "\"position\": \"9\",\n" +
                "\"option\": {\n" +
                "\"_id\": \"59d94d5ff5d04d1d3e12ee8d\",\n" +
                "\"type\": \"GENERIC\",\n" +
                "\"text\": {\n" +
                "\"english\": \"Pond/Oorani\",\n" +
                "\"tamil\": \"குளம் / ஊரணி\",\n" +
                "\"_id\": \"59d94d5ff5d04d1d3e12ee8e\",\n" +
                "\"modifiedAt\": \"2017-10-07T21:55:43.542Z\"\n" +
                "},\n" +
                "\"__v\": 0,\n" +
                "\"modifiedAt\": \"2017-10-07T21:55:43.542Z\"\n" +
                "},\n" +
                "\"_id\": \"59d94d60f5d04d1d3e12f529\"\n" +
                "}";
        // add the options
        addMockAnswerToQuestion(mockOption, "59d94d5ff5d04d1d3e12ee8d", question_2_1_7_4_1);

        // next question
        Question question_2_1_7_4_4 = question_2_1_7_4.getChildren().get(3);
        mockOption = "{\n" +
                "\"position\": \"1\",\n" +
                "\"option\": {\n" +
                "\"_id\": \"59d94d5ff5d04d1d3e12ee9b\",\n" +
                "\"type\": \"GENERIC\",\n" +
                "\"text\": {\n" +
                "\"english\": \"Compound wall\",\n" +
                "\"tamil\": \"சுற்றுசுவர் \",\n" +
                "\"_id\": \"59d94d5ff5d04d1d3e12ee9c\",\n" +
                "\"modifiedAt\": \"2017-10-07T21:55:43.546Z\"\n" +
                "},\n" +
                "\"__v\": 0,\n" +
                "\"modifiedAt\": \"2017-10-07T21:55:43.546Z\"\n" +
                "},\n" +
                "\"_id\": \"59d94d61f5d04d1d3e12f7cb\"\n" +
                "}";
        // add the options
        addMockAnswerToQuestion(mockOption, "59d94d5ff5d04d1d3e12ee9b", question_2_1_7_4_4);

        questionFlowHelper.setCurrent(question_2_1_7_4);
        testFlow(questionFlowHelper, new String[]{"2.1.7"}); // skip all the answered questions and apply skip pattern
    }

    private void testFlow(QuestionFlowHelper questionFlowHelper, String[] rawQuestionNumbers) {
        for (int i = 0; i < rawQuestionNumbers.length; i++) {
            questionFlowHelper.getNext();
            questionFlowHelper.getCurrent().addAnswer(new Answer(new ArrayList<Option>() /* empty options list */, null));
            assertThat(questionFlowHelper.getCurrent().getRawNumber(), is(rawQuestionNumbers[i]));
        }
    }

    private void addMockAnswerToQuestion(String mockOptionJson, String optionID, Question to) {
        JsonParser jsonParser = new JsonParser();
        JsonObject mockJson = jsonParser.parse(mockOptionJson).getAsJsonObject();
        ArrayList<Option> mockOptions = new ArrayList<>();
        Option o = new Option(mockJson);
        assertThat(o.getId(), is(optionID));
        mockOptions.add(o);

        // add the options
        to.addAnswer(new Answer(mockOptions, to));
    }

}
