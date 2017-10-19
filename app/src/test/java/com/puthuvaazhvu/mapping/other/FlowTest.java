package com.puthuvaazhvu.mapping.other;

import com.puthuvaazhvu.mapping.modals.ModalHelpers;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.IFlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.FlowImplementation;
import com.puthuvaazhvu.mapping.views.helpers.ResponseData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    private Survey survey;
    private FlowImplementation flowImplementation;
    private Question root;

    @Before
    public void setup() {
        survey = ModalHelpers.getSurvey(this);
        root = survey.getQuestionList().get(0);
        flowImplementation = new FlowImplementation(root);
    }

    @Test
    public void test_update_method() {
        Question question = root.getChildren().get(0);
        flowImplementation.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("1"));

        // add mock answer
        OptionData responseData = OptionData.adapter(question);
        responseData.setAnswerData(new InputAnswerData(question.getId(), question.getTextString(), "TEST"));
        data.setResponseData(responseData);

        Question current = flowImplementation.update(ResponseData.adapter(data)).getCurrent();

        assertThat(current.getAnswers().size(), is(1));
        assertThat(current.getAnswers().get(0).getOptions().get(0).getTextString(), is("TEST"));
        assertThat(current.getAnswers().get(0).getChildren().size(), is(question.getChildren().size()));
        assertThat(current.getAnswers().get(0).getChildren().get(0).getParent(), is(question));

        //                           -- answer scope single ---

        responseData.setAnswerData(new InputAnswerData(question.getId(), question.getTextString(), "TEST1"));
        data.setResponseData(responseData);

        current = flowImplementation.update(ResponseData.adapter(data)).getCurrent();

        assertThat(current.getAnswers().size(), is(1));
        assertThat(current.getAnswers().get(0).getOptions().get(0).getTextString(), is("TEST1"));
        assertThat(current.getAnswers().get(0).getChildren().size(), is(question.getChildren().size()));

        //                           -- answer scope multiple ---
        question = root.getChildren().get(1).getChildren().get(1).getChildren().get(6).getChildren().get(0);
        flowImplementation.setCurrentForTesting(question);
        data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("2.1.7.3"));

        // add mock answer
        responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST", "0"));
        data.setResponseData(responseData);

        current = flowImplementation.update(ResponseData.adapter(data)).getCurrent();
        assertThat(current.getAnswers().get(0).getOptions().get(0).getId(), is("1"));

        // add mock answer
        responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "2", "TEST1", "0"));
        data.setResponseData(responseData);

        current = flowImplementation.update(ResponseData.adapter(data)).getCurrent();

        assertThat(current.getAnswers().size(), is(2));
        assertThat(current.getAnswers().get(1).getOptions().get(0).getId(), is("2"));
    }

    @Test
    public void test_moveToIndex_method() {
        Question question = root.getChildren().get(0);
        flowImplementation.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        // add mock answer
        OptionData responseData = OptionData.adapter(question);
        responseData.setAnswerData(new InputAnswerData(question.getId(), question.getTextString(), "TEST"));
        data.setResponseData(responseData);

        assertThat(question.getRawNumber(), is("1"));

        flowImplementation.update(ResponseData.adapter(data));

        Question current = flowImplementation.moveToIndex(3).getCurrent();

        assertThat(current.getAnswers().isEmpty(), is(true));
        assertThat(current.getRawNumber(), is("1.5"));
    }

    @Test
    public void test_getNext_gridFlow() {
        Question question = root.getChildren().get(1).getChildren().get(1).getChildren().get(6);
        flowImplementation.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("2.1.7"));

        // add mock answer
        OptionData responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST1", "0"));
        data.setResponseData(responseData);

        Question current;

        flowImplementation.update(ResponseData.adapter(data)).getCurrent();

        // get the next question
        IFlowHelper.FlowData flowData = flowImplementation.getNext();

        assertThat(flowData, notNullValue());
        assertThat(flowData.flowType, is(FlowType.GRID));

        // move to the first child question (mock click of the first question in the grid)
        current = flowImplementation.moveToIndex(0).getCurrent();
        assertThat(current.getRawNumber(), is("2.1.7.3"));

        // add mock answer
        data = QuestionData.adapter(flowImplementation.getCurrent());

        responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST1", "0"));
        data.setResponseData(responseData);

        flowImplementation.update(ResponseData.adapter(data));

        // finish the current question
        flowImplementation.finishCurrent();

        current = flowImplementation.getCurrent();
        assertThat(current.getRawNumber(), is("2.1.7"));
    }

    @Test
    public void test_loopFlow_multiple() {
        Question question = root.getChildren().get(1).getChildren().get(1);
        flowImplementation.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("2.1"));

        // mock answer
        OptionData responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST", "0"));
        data.setResponseData(responseData);

        Question current = flowImplementation.update(ResponseData.adapter(data)).getCurrent();

        assertThat(current.getRawNumber(), is("2.1"));

        // add mock answer to all the children
        ArrayList<Question> children = current.getAnswers().get(0).getChildren();

        assertThat(children.size(), is(question.getChildren().size()));

        for (Question q : children) {
            q.setFinished(true);
        }

        flowImplementation.setCurrentForTesting(current);
        IFlowHelper.FlowData flowData = flowImplementation.getNext();

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
        flowImplementation.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        assertThat(question.getType(), is("ROOT"));

        // add mock answer
        OptionData responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "DUMMY FOR ROOT", "0"));
        data.setResponseData(responseData);

        Question current = flowImplementation.update(ResponseData.adapter(data)).getCurrent();

        // add mock answer to all the children
        ArrayList<Question> children = current.getAnswers().get(0).getChildren();

        assertThat(children.size(), is(question.getChildren().size()));

        children.get(0).setFinished(true);  //set the 1st child finished.

        // *** 2 ***
        question = children.get(1);
        flowImplementation.setCurrentForTesting(question);
        data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("2"));

        // add mock answer
        responseData = OptionData.adapter(question);
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "TEST", "0"));
        data.setResponseData(responseData);

        current = flowImplementation.update(ResponseData.adapter(data)).getCurrent();

        children = current.getAnswers().get(0).getChildren();

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

            flowImplementation.setCurrentForTesting(question);
            flowImplementation.update(ResponseData.adapter(data));

            if (c.getRawNumber().equals("2.1")) {
                c.setFinished(true);
            }
        }

        flowImplementation.setCurrentForTesting(current); // current should be populated by now
        IFlowHelper.FlowData flowData = flowImplementation.getNext();

        assertThat(flowData.flowType, is(FlowType.SINGLE));
        assertThat(flowData.question.getRawNumber(), is("2"));
    }

    @Test
    public void test_getNext_skipFlow() {
        Question question = root.getChildren().get(1).getChildren().get(1).getChildren().get(6).getChildren().get(0);
        flowImplementation.setCurrentForTesting(question);
        QuestionData data = QuestionData.adapter(question);

        assertThat(question.getRawNumber(), is("2.1.7.3"));

        // add mock answer
        OptionData responseData = OptionData.adapter(question);
        // the option position is important as inside the code only that is checked for skip pattern
        responseData.setAnswerData(new SingleAnswerData(question.getId(), question.getTextString(), "1", "NO", "0"));
        data.setResponseData(responseData);

        flowImplementation.update(ResponseData.adapter(data));

        IFlowHelper.FlowData flowData = flowImplementation.getNext();

        assertThat(flowData.flowType, is(FlowType.GRID));
        assertThat(flowData.question.getRawNumber(), is("2.1.7"));
    }
}
