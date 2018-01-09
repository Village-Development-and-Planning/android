package com.puthuvaazhvu.mapping.other.flow;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.helpers.ModalHelpers;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.next_flow.IFlow;
import com.puthuvaazhvu.mapping.views.helpers.next_flow.FlowImplementation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Random;

import static junit.framework.Assert.assertSame;
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
public class FlowImplementationTest {

    private Survey survey;
    private FlowImplementation flowImplementation;
    private Question root;

    @Before
    public void setup() {
        survey = ModalHelpers.getSurvey(this);
        root = survey.getRootQuestion();
        flowImplementation = new FlowImplementation(root);
    }

    @Test
    public void testWithSnapshotPath() {
        Survey survey = ModalHelpers.getAnsweredSurvey(this);

        assertThat(survey.getId(), is("5a08957bad04f82a15a0e974"));

        String snapShotPath = "0,0,1,0";

        flowImplementation = new FlowImplementation(survey.getRootQuestion(), snapShotPath);

        Question current = flowImplementation.getCurrent();

        assertThat(current, is(notNullValue()));
        assertThat(current.getRawNumber(), is("2"));
        assertThat(current.getAnswers().size(), is(1));
    }

    @Test
    public void test_update_method() {
        Question question = root.getChildren().get(0);
        flowImplementation.setCurrent(question);

        assertThat(question.getRawNumber(), is("1"));

        Question current = flowImplementation.update(ModalHelpers.getDummyOptions()).getCurrent();

        assertThat(current.getAnswers().size(), is(1));
        assertThat(current.getAnswers().get(0).getChildren().size(), is(question.getChildren().size()));
        assertThat(current.getAnswers().get(0).getChildren().get(0).getParent(), is(question));

        //                           -- answer scope single ---

        current = flowImplementation.update(ModalHelpers.getDummyOptions()).getCurrent();

        assertThat(current.getAnswers().size(), is(1));
        assertThat(current.getAnswers().get(0).getChildren().size(), is(question.getChildren().size()));

        //                           -- answer scope multiple ---
        question = root.getChildren().get(1).getChildren().get(1).getChildren().get(6).getChildren().get(0);
        flowImplementation.setCurrent(question);

        assertThat(question.getRawNumber(), is("2.1.7.3"));

        // add mock answer
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option(
                "1",
                "",
                null,
                "",
                ""
        ));

        current = flowImplementation.update(options).getCurrent();
        assertThat(current.getAnswers().get(0).getOptions().get(0).getId(), is("1"));

        options = new ArrayList<>();
        options.add(new Option(
                "2",
                "",
                null,
                "",
                ""
        ));

        current = flowImplementation.update(options).getCurrent();
        assertThat(current.getAnswers().size(), is(2));
        assertThat(current.getAnswers().get(1).getOptions().get(0).getId(), is("2"));
    }

    @Test
    public void test_moveToIndex_method() {
        Question question = root.getChildren().get(0);
        flowImplementation.setCurrent(question);

        assertThat(question.getRawNumber(), is("1"));

        flowImplementation.update(ModalHelpers.getDummyOptions());

        Question current = flowImplementation.moveToIndex(3).getCurrent();

        assertThat(current.getAnswers().isEmpty(), is(true));
        assertThat(current.getRawNumber(), is("1.5"));
    }

    @Test
    public void test_getNext_gridFlow() {
        Question question = root.getChildren().get(1).getChildren().get(1).getChildren().get(6);
        flowImplementation.setCurrent(question);

        assertThat(question.getRawNumber(), is("2.1.7"));

        Question current;

        flowImplementation.update(ModalHelpers.getDummyOptions()).getCurrent();

        // get the next question
        IFlow.FlowData flowData = flowImplementation.getNext();

        assertThat(flowData, notNullValue());
        assertThat(flowData.flowType, is(FlowType.GRID));

        // move to the first child question (mock click of the first question in the grid)
        current = flowImplementation.moveToIndex(0).getCurrent();
        assertThat(current.getRawNumber(), is("2.1.7.3"));

        flowImplementation.update(ModalHelpers.getDummyOptions());

        // finish the current question
        flowImplementation.finishCurrent();

        current = flowImplementation.getCurrent();
        assertThat(current.getRawNumber(), is("2.1.7"));
    }

    @Test
    public void test_loopFlow_multiple() {
        Question question = root.getChildren().get(1).getChildren().get(1);
        flowImplementation.setCurrent(question);

        assertThat(question.getRawNumber(), is("2.1"));

        // mock answer
        Question current = flowImplementation.update(ModalHelpers.getDummyOptions()).getCurrent();

        assertThat(current.getRawNumber(), is("2.1"));

        // add mock answer to all the children
        ArrayList<Question> children = current.getAnswers().get(0).getChildren();

        assertThat(children.size(), is(question.getChildren().size()));

        for (Question q : children) {
            q.setFinished(true);
        }

        flowImplementation.setCurrent(current);
        IFlow.FlowData flowData = flowImplementation.getNext();

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
        flowImplementation.setCurrent(question);

        assertThat(question.getType(), is("ROOT"));

        Question current = flowImplementation.update(ModalHelpers.getDummyOptions()).getCurrent();

        // add mock answer to all the children
        ArrayList<Question> children = current.getAnswers().get(0).getChildren();

        assertThat(children.size(), is(question.getChildren().size()));

        children.get(0).setFinished(true);  //set the 1st child finished.

        // *** 2 ***
        question = children.get(1);
        flowImplementation.setCurrent(question);

        assertThat(question.getRawNumber(), is("2"));

        // add mock answer

        current = flowImplementation.update(ModalHelpers.getDummyOptions()).getCurrent();

        children = current.getAnswers().get(0).getChildren();

        assertThat(children.size(), is(question.getChildren().size()));

        // iterate through the children inside answers
        Random random = new Random();
        for (Question c : children) {
            question = c;

            flowImplementation.setCurrent(question);
            flowImplementation.update(ModalHelpers.getDummyOptions());

            if (c.getRawNumber().equals("2.1")) {
                c.setFinished(true);
            }
        }

        flowImplementation.setCurrent(current); // current should be populated by now
        IFlow.FlowData flowData = flowImplementation.getNext();

        assertThat(flowData.flowType, is(FlowType.SINGLE));
        assertThat(flowData.question.getRawNumber(), is("2"));
    }

    @Test
    public void test_multipleOptionsSkip() {
        JsonObject jsonObject = ModalHelpers.getJson(this, "multiple_options_skip.json");
        Question question = Question.populateQuestion(jsonObject);

        assertThat(question.getRawNumber(), is("2.1.6.5"));
        flowImplementation.setCurrent(question);

        ArrayList<Option> response = new ArrayList<>();
        response.add(new Option("", "GENERIC", null, "", "1"));

        flowImplementation.update(response);

        Answer answer_2_1_6_5 = question.getAnswers().get(0);

        Question q_2_1_6_5_1 = answer_2_1_6_5.getChildren().get(0);
        assertThat(q_2_1_6_5_1.getRawNumber(), is("2.1.6.5.1"));

        boolean result = FlowImplementation.doesSkipPatternMatchInQuestion(
                q_2_1_6_5_1.getFlowPattern().getPreFlow().getOptionSkip(),
                answer_2_1_6_5
        );

        assertThat(result, is(true));

        flowImplementation.setCurrent(q_2_1_6_5_1);

        response = new ArrayList<>();
        response.add(new Option("", "GENERIC", null, "", "1"));
        response.add(new Option("", "GENERIC", null, "", "2"));
        response.add(new Option("", "GENERIC", null, "", "3"));
        response.add(new Option("", "GENERIC", null, "", "4"));
        response.add(new Option("", "GENERIC", null, "", "5"));

        flowImplementation.update(response);

        Answer answer_2_1_6_5_1 = q_2_1_6_5_1.getAnswers().get(0);

        Question q_2_1_6_5_2 = answer_2_1_6_5.getChildren().get(1);
        assertThat(q_2_1_6_5_2.getRawNumber(), is("2.1.6.5.2"));

        result = FlowImplementation.doesSkipPatternMatchInQuestion(
                q_2_1_6_5_2.getFlowPattern().getPreFlow().getOptionSkip(),
                answer_2_1_6_5_1
        );

        assertThat(result, is(true));

        flowImplementation.setCurrent(q_2_1_6_5_1);

        response = new ArrayList<>();
        response.add(new Option("", "GENERIC", null, "", "4"));

        flowImplementation.update(response);

        answer_2_1_6_5_1 = q_2_1_6_5_1.getAnswers().get(0);

        Question q_2_1_6_5_3 = answer_2_1_6_5.getChildren().get(2);
        assertThat(q_2_1_6_5_3.getRawNumber(), is("2.1.6.5.3"));

        result = FlowImplementation.doesSkipPatternMatchInQuestion(
                q_2_1_6_5_3.getFlowPattern().getPreFlow().getOptionSkip(),
                answer_2_1_6_5_1
        );

        assertThat(result, is(false));

        // household
        jsonObject = ModalHelpers.getJson(this, "house_hold.json");
        question = Question.populateQuestion(jsonObject);

        ModalHelpers.setDummyAnswersForAllQuestions(question);

        Question question_2_7_3 = question.findInTree(new Question.QuestionTreeSearchPredicate() {
            @Override
            public boolean evaluate(Question question) {
                return question.getRawNumber() != null && question.getRawNumber().equals("2.7.3");
            }
        });

        assertThat(question_2_7_3.getRawNumber(), is("2.7.3"));
        flowImplementation.setCurrent(question_2_7_3);

        response = new ArrayList<>();
        response.add(new Option("", "GENERIC", null, "", "1"));

        flowImplementation.update(response);

        Question question_8_5 = question.getAnswers().get(0).getChildren().get(50);

        assertThat(question_8_5.getRawNumber(), is("8.5"));
        flowImplementation.setCurrent(question_8_5);

        result = FlowImplementation.shouldSkipBasedOnSkipPattern(question_8_5);

        assertThat(result, is(true));
    }

    @Test
    public void test_getNext_skipFlow() {
        Question question;

        question = root.getChildren().get(1).getChildren().get(1).getChildren().get(6);

        assertThat(question.getRawNumber(), is("2.1.7"));

        flowImplementation.setCurrent(question);

        flowImplementation.update(ModalHelpers.getDummyOptions());

        question = flowImplementation.moveToIndex(0).getCurrent();

        assertThat(question.getRawNumber(), is("2.1.7.3"));

        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option(
                "",
                "",
                new Text("", "NO", "NO", ""),
                "",
                "0"
        ));

        flowImplementation.update(options);

        IFlow.FlowData flowData = flowImplementation.getNext();

        assertThat(flowData.flowType, is(FlowType.GRID));
        assertThat(flowData.question.getRawNumber(), is("2.1.7"));
    }

    @Test
    public void test_getNext_cascadeFlow() {
        Question question = root.getChildren().get(0);

        assertThat(question.getRawNumber(), is("1"));

        flowImplementation.setCurrent(question);

        flowImplementation.update(ModalHelpers.getDummyOptions());

        Question parent = flowImplementation.getCurrent();

        assertThat(flowImplementation.getCurrent().getAnswers().size(), is(1));

        IFlow.FlowData flowData = flowImplementation.getNext();

        assertThat(flowData.question.getRawNumber(), is("1.2"));

        assertSame(flowData.question.getParent(), parent);
    }

    @Test
    public void testBackNavigation() {
        JsonObject jsonObject = ModalHelpers.getJson(this, "back_test.json");
        Question question = Question.populateQuestion(jsonObject);

        assertThat(question.isRoot(), is(true));

        Question question_3_5 = question.findInTree(new Question.QuestionTreeSearchPredicate() {
            @Override
            public boolean evaluate(Question question) {
                return question.getRawNumber() != null && question.getRawNumber().equals("3.5");
            }
        });

        assertThat(question_3_5.getRawNumber(), is("3.5"));

        flowImplementation.setCurrent(question_3_5);
        flowImplementation.update(ModalHelpers.getDummyOptions());
        flowImplementation.getNext();

        Question q_3_5_1 = flowImplementation.getCurrent();

        assertThat(q_3_5_1.getRawNumber(), is("3.5.1"));

        flowImplementation.update(ModalHelpers.getDummyOptions());
        flowImplementation.getNext();

        Question q_3_5_2 = flowImplementation.getCurrent();

        assertThat(q_3_5_2.getRawNumber(), is("3.5.2"));

        IFlow.FlowData previousData = flowImplementation.getPrevious();

        assertThat(previousData.question.getRawNumber(), is("3.5.2"));
        assertSame(previousData.question, q_3_5_2);
        assertThat(previousData.flowType, is(FlowType.SINGLE));
    }
}
