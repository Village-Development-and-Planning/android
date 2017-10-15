package com.puthuvaazhvu.mapping.other;

import android.test.suitebuilder.annotation.Suppress;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.SurveyDataModelTest;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Flow.PreFlow;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.helpers.flow.FlowHelperBase;
import com.puthuvaazhvu.mapping.views.helpers.flow.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.flow.SurveyFlowHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;

/**
 * Created by muthuveerappans on 10/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class FlowTest {

    private SurveyDataModelTest surveyDataModelTest;
    private Survey survey;
    private FlowHelperBase surveyFlow;
    private Question root;

    @Before
    public void setup() {
        surveyDataModelTest = new SurveyDataModelTest();

        surveyDataModelTest.testSurveyModel();
        survey = surveyDataModelTest.survey;
        root = survey.getQuestionList().get(0);
        surveyFlow = new SurveyFlowHelper(root);
    }

    @Test
    public void test_update_method() {
        Question question = root.getChildren().get(0);
        surveyFlow.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("1"));

        // add mock answer
        OptionData responseData = OptionData.adapter(question);
        responseData.setAnswerData(new InputAnswerData(question.getId(), question.getTextString(), "TEST"));
        data.setResponseData(responseData);

        Question current = surveyFlow.update(data).getCurrent();

        assertThat(current.getAnswer().size(), is(1));
        assertThat(current.getAnswer().get(0).getOptions().get(0).getTextString(), is("TEST"));
        assertThat(current.getAnswer().get(0).getChildren().size(), is(question.getChildren().size()));
        assertThat(current.getAnswer().get(0).getChildren().get(0).getParent(), is(question));

        //                           -- answer scope single ---

        responseData.setAnswerData(new InputAnswerData(question.getId(), question.getTextString(), "TEST1"));
        data.setResponseData(responseData);

        current = surveyFlow.update(data).getCurrent();

        assertThat(current.getAnswer().size(), is(1));
        assertThat(current.getAnswer().get(0).getOptions().get(0).getTextString(), is("TEST1"));
        assertThat(current.getAnswer().get(0).getChildren().size(), is(question.getChildren().size()));

        //                           -- answer scope multiple ---
        question = root.getChildren().get(1).getChildren().get(1).getChildren().get(6).getChildren().get(0);
        surveyFlow.setCurrentForTesting(question);
        data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("2.1.7.3"));

        // add mock answer
        responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST", "0"));
        data.setResponseData(responseData);

        current = surveyFlow.update(data).getCurrent();
        assertThat(current.getAnswer().get(0).getOptions().get(0).getId(), is("1"));

        // add mock answer
        responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "2", "TEST1", "0"));
        data.setResponseData(responseData);

        current = surveyFlow.update(data).getCurrent();

        assertThat(current.getAnswer().size(), is(2));
        assertThat(current.getAnswer().get(1).getOptions().get(0).getId(), is("2"));
    }

    @Test
    public void test_moveToIndex_method() {
        Question question = root.getChildren().get(0);
        surveyFlow.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        // add mock answer
        OptionData responseData = OptionData.adapter(question);
        responseData.setAnswerData(new InputAnswerData(question.getId(), question.getTextString(), "TEST"));
        data.setResponseData(responseData);

        assertThat(question.getRawNumber(), is("1"));

        surveyFlow.update(data);

        Question current = surveyFlow.moveToIndex(3).getCurrent();

        assertThat(current.getAnswer().isEmpty(), is(true));
        assertThat(current.getRawNumber(), is("1.5"));
    }

    @Test
    public void test_getNext_gridFlow() {
        Question question = root.getChildren().get(1).getChildren().get(1).getChildren().get(6);
        surveyFlow.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("2.1.7"));

        // add mock answer
        OptionData responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST1", "0"));
        data.setResponseData(responseData);

        Question current;

        surveyFlow.update(data).getCurrent();

        // get the next question
        FlowHelperBase.FlowData flowData = surveyFlow.getNext();

        assertThat(flowData, notNullValue());
        assertThat(flowData.flowType, is(FlowType.GRID));

        // move to the first child question (mock click of the first question in the grid)
        current = surveyFlow.moveToIndex(0).getCurrent();
        assertThat(current.getRawNumber(), is("2.1.7.3"));

        // add mock answer
        data = QuestionData.adapter(surveyFlow.getCurrent());

        responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST1", "0"));
        data.setResponseData(responseData);

        surveyFlow.update(data);

        // finish the current question
        surveyFlow.finishCurrentQuestion();

        current = surveyFlow.getCurrent();
        assertThat(current.getRawNumber(), is("2.1.7"));
    }

    @Test
    public void test_loopFlow_multiple() {
        Question question = root.getChildren().get(1).getChildren().get(1);
        surveyFlow.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("2.1"));

        // mock answer
        OptionData responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST", "0"));
        data.setResponseData(responseData);

        Question current = surveyFlow.update(data).getCurrent();

        assertThat(current.getRawNumber(), is("2.1"));

        // add mock answer to all the children
        ArrayList<Question> children = current.getAnswer().get(0).getChildren();

        assertThat(children.size(), is(question.getChildren().size()));

        for (Question q : children) {
            q.setFinished(true);
        }

        surveyFlow.setCurrentForTesting(current);
        FlowHelperBase.FlowData flowData = surveyFlow.getNext();

        assertThat(flowData.flowType, is(FlowType.SINGLE));
        assertThat(flowData.question.getRawNumber(), is("2.1"));
    }

    @Test
    public void test_loopFlow_options() {
        //                          -- answer scope options --
        /* set up
            1. set the other 1st level children excluding the child in action to finished
            2. Set the child's answer to a random value. Answer count should be 1
            3. Check if that child is skipped in the next iteration
         */

        // *** ROOT ***
        Question question = root;
        surveyFlow.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        assertThat(question.getType(), is("ROOT"));

        // add mock answer
        OptionData responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST", "0"));
        data.setResponseData(responseData);

        Question current = surveyFlow.update(data).getCurrent();

        // add mock answer to all the children
        ArrayList<Question> children = current.getAnswer().get(0).getChildren();

        assertThat(children.size(), is(question.getChildren().size()));

        children.get(0).setFinished(true);  //set the 1st child finished.

        // *** 2 ***
        question = children.get(1);
        surveyFlow.setCurrentForTesting(question);
        data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("2"));

        // add mock answer
        responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST", "0"));
        data.setResponseData(responseData);

        current = surveyFlow.update(data).getCurrent();

        children = current.getAnswer().get(0).getChildren();

        assertThat(children.size(), is(question.getChildren().size()));

        // iterate through the children inside answers
        Random random = new Random();
        for (Question c : children) {
            question = c;
            data = QuestionData.adapter(question);

            responseData = OptionData.adapter(question);
            responseData.setAnswerData(new SingleAnswerData(question.getId()
                    , question.getTextString()
                    , "" + random.nextInt(100)
                    , "TEST " + random.nextInt(100)
                    , "0"));
            data.setResponseData(responseData);

            surveyFlow.setCurrentForTesting(question);
            surveyFlow.update(data);

            if (c.getRawNumber().equals("2.1")) {
                c.setFinished(true);
            }
        }

        surveyFlow.setCurrentForTesting(current); // current should be populated by now
        FlowHelperBase.FlowData flowData = surveyFlow.getNext();

        assertThat(flowData.flowType, is(FlowType.END));
        assertThat(flowData.question, nullValue());
    }

    @Test
    public void test_getNext_skipFlow() {
        Question question = root.getChildren().get(1).getChildren().get(1).getChildren().get(6).getChildren().get(0);
        surveyFlow.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("2.1.7.3"));

        // add mock answer
        OptionData responseData = OptionData.adapter(question);
        // the option position is important as inside the code only that is checked for skip pattern
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "NO", "0"));
        data.setResponseData(responseData);

        surveyFlow.update(data);

        FlowHelperBase.FlowData flowData = surveyFlow.getNext();

        assertThat(flowData.flowType, is(FlowType.GRID));
        assertThat(flowData.question.getRawNumber(), is("2.1.7"));
    }
}
