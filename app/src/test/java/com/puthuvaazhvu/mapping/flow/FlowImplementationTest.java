package com.puthuvaazhvu.mapping.flow;

import com.puthuvaazhvu.mapping.helpers.DataHelpers;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogicImplementation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

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

    private void addDummyAnswersToChildren(Question node) {
        Answer answer = new Answer(Question.dummyOptions(), node, System.currentTimeMillis());
        node.setAnswer(answer);

        for (Question child : node.getLatestAnswer().getChildren()) {
            addDummyAnswersToChildren(child);
        }
    }

    private Question moveTo(String path, Question question) {
        return Question.moveToQuestion(path, question);
    }

    @Test
    public void test_update() {
        Survey survey = DataHelpers.getSurvey(this, "flow_data.json");
        Question root = survey.getRootQuestion();

        assertThat(survey.getId(), is("5a5ef00e0526a120e341d7da"));

        addDummyAnswersToChildren(root);

        FlowLogic flowLogic = new FlowLogicImplementation(root);
        Question question = moveTo("0,0,1,0", root);
        assertThat(question.getRawNumber(), is("1.2"));
        flowLogic.setCurrent(question, FlowLogic.FlowData.FlowUIType.DEFAULT);

        ArrayList<Option> options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "")
        );

        flowLogic.update(options);

        assertThat(flowLogic.getCurrent().question.getAnswers().size(), is(1));
        assertThat(flowLogic.getCurrent().question.getLatestAnswer().getOptions().get(0).getType(), is("TEST_DATA"));
    }

    @Test
    public void test_addAnswer() {
        Survey survey = DataHelpers.getSurvey(this, "flow_data.json");
        Question root = survey.getRootQuestion();

        assertThat(survey.getId(), is("5a5ef00e0526a120e341d7da"));

        addDummyAnswersToChildren(root);

        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation(root);
        Question question = moveTo("0,0,1,0", root);
        assertThat(question.getRawNumber(), is("1.2"));
        flowLogicImplementation.setCurrent(question, FlowLogic.FlowData.FlowUIType.DEFAULT);

        flowLogicImplementation.addAnswer(Question.dummyOptions(), question);
        assertThat(flowLogicImplementation.getCurrent().question.getAnswers().size(), is(1));
    }

    @Test
    public void test_addAnswersToChildren() {
        Survey survey = DataHelpers.getSurvey(this, "flow_data.json");
        Question root = survey.getRootQuestion();

        assertThat(survey.getId(), is("5a5ef00e0526a120e341d7da"));

        addDummyAnswersToChildren(root);

        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation(root);
        flowLogicImplementation.addDummyAnswersToChildren(root);

        assertThat(root.getAnswers().size(), is(1));
    }

    @Test
    public void test_finishCurrent() {
        Survey survey = DataHelpers.getSurvey(this, "flow_data.json");
        Question root = survey.getRootQuestion();

        assertThat(survey.getId(), is("5a5ef00e0526a120e341d7da"));

        addDummyAnswersToChildren(root);

        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation(root);
        Question question = moveTo("0,0,1,0", root);
        assertThat(question.getRawNumber(), is("1.2"));
        flowLogicImplementation.setCurrent(question, FlowLogic.FlowData.FlowUIType.DEFAULT);

        flowLogicImplementation.finishCurrent();

        assertSame(flowLogicImplementation.getCurrent().question, question.getParentAnswer().getQuestionReference());
    }

    @Test
    public void test_getNext() {
        Survey survey = DataHelpers.getSurvey(this, "flow_data.json");
        Question root = survey.getRootQuestion();

        assertThat(survey.getId(), is("5a5ef00e0526a120e341d7da"));

        addDummyAnswersToChildren(root);

        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation(root);

        // normal flow
        flowLogicImplementation.getNext();
        assertThat(flowLogicImplementation.getCurrent().question.getRawNumber(), is("1.1"));

        // test shown together
        survey = DataHelpers.getSurvey(this, "shown_together_question.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));

        flowLogicImplementation.setCurrent(root, FlowLogic.FlowData.FlowUIType.DEFAULT);
        Question nextQuestion = flowLogicImplementation.getNext().question;

        assertThat(nextQuestion.getAnswers().size(), is(1));

        // test grid question
        survey = DataHelpers.getSurvey(this, "grid_question.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));

        flowLogicImplementation.setCurrent(root, FlowLogic.FlowData.FlowUIType.DEFAULT);

        nextQuestion = flowLogicImplementation.getNext().question;
        assertThat(nextQuestion.getAnswers().size(), is(1));

        flowLogicImplementation.moveToIndexInChild(0);
        assertThat(flowLogicImplementation.getCurrent().question.getRawNumber(), is("2.1.7.3"));

        // test skip pattern
        survey = DataHelpers.getSurvey(this, "multiple_options_skip.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));

        flowLogicImplementation = new FlowLogicImplementation(root);

        nextQuestion = flowLogicImplementation.getNext().question;

        assertThat(nextQuestion.getRawNumber(), is("2.1.6.5"));
        assertThat(nextQuestion.getAnswers().size(), is(1));

        ArrayList<Option> options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "1")
        );

        flowLogicImplementation.update(options);
        assertThat(flowLogicImplementation.getCurrent().question.getAnswers().size(), is(1));

        nextQuestion = flowLogicImplementation.getNext().question;
        assertThat(nextQuestion.getRawNumber(), is("2.1.6.5.1"));
        assertThat(nextQuestion.getAnswers().size(), is(1));

        // test skip false
        survey = DataHelpers.getSurvey(this, "multiple_options_skip.json");
        root = survey.getRootQuestion();
        flowLogicImplementation = new FlowLogicImplementation(root);

        nextQuestion = flowLogicImplementation.getNext().question;

        assertThat(nextQuestion.getRawNumber(), is("2.1.6.5"));
        assertThat(nextQuestion.getAnswers().size(), is(1));

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "2")
        );

        flowLogicImplementation.update(options);
        assertThat(flowLogicImplementation.getCurrent().question.getAnswers().size(), is(1));

        FlowLogic.FlowData flowData = flowLogicImplementation.getNext();
        assertThat(flowData.flowType, is(FlowLogic.FlowData.FlowUIType.END));
    }

    @Test
    public void test_moveToIndexInChild() {
        Survey survey = DataHelpers.getSurvey(this, "flow_data.json");
        Question root = survey.getRootQuestion();

        assertThat(survey.getId(), is("5a5ef00e0526a120e341d7da"));

        addDummyAnswersToChildren(root);

        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation(root);
        flowLogicImplementation.moveToIndexInChild(13);

        assertThat(flowLogicImplementation.getCurrent().question.getRawNumber(), is("2"));
        assertThat(flowLogicImplementation.getCurrent().question.getAnswers().size(), is(1));
    }

    @Test
    public void test_getPrevious() {
        FlowLogic.FlowData flowData;

        Survey survey = DataHelpers.getSurvey(this, "back_test.json");
        Question root = survey.getRootQuestion();
        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation(root);

        flowData = flowLogicImplementation.getNext();
        assertThat(flowData.question.getRawNumber(), is("2.1.7"));
        assertThat(flowData.flowType, is(FlowLogic.FlowData.FlowUIType.DEFAULT));

        flowData = flowLogicImplementation.getPrevious();
        assertThat(flowData.flowType, is(FlowLogic.FlowData.FlowUIType.DEFAULT));
        assertThat(flowData.question.getRawNumber(), is("2.1.7"));

        flowData = flowLogicImplementation.getNext();
        assertThat(flowData.question.getRawNumber(), is("2.1.7"));
        assertThat(flowData.flowType, is(FlowLogic.FlowData.FlowUIType.GRID));

        flowData = flowLogicImplementation.moveToIndexInChild(0).getCurrent();
        assertThat(flowData.question.getRawNumber(), is("2.1.7.3"));
        assertThat(flowData.flowType, is(FlowLogic.FlowData.FlowUIType.DEFAULT));

        flowData = flowLogicImplementation.getPrevious();
        assertThat(flowData.flowType, is(FlowLogic.FlowData.FlowUIType.GRID));
        assertThat(flowData.question.getRawNumber(), is("2.1.7"));
    }

    @Test
    public void test_childFlow() {
        Survey survey;
        Question root;
        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation();
        Question nextQuestion;

        // test grid question
        survey = DataHelpers.getSurvey(this, "grid_question.json");
        root = survey.getRootQuestion();
        root.setAnswer(new Answer(Question.dummyOptions(), root));
        assertThat(root.getType(), is("ROOT"));

        flowLogicImplementation.setCurrent(root, FlowLogic.FlowData.FlowUIType.DEFAULT);

        FlowLogic.FlowData flowData = flowLogicImplementation.childFlow(root);
        nextQuestion = flowData.question;
        assertThat(nextQuestion.getRawNumber(), is("2.1.7"));
        assertThat(flowData.flowType, is(FlowLogic.FlowData.FlowUIType.DEFAULT));

        flowData = flowLogicImplementation.childFlow(nextQuestion);
        nextQuestion = flowData.question;
        assertThat(nextQuestion.getRawNumber(), is("2.1.7"));
        assertThat(flowData.flowType, is(FlowLogic.FlowData.FlowUIType.GRID));

        // test normal flow
        survey = DataHelpers.getSurvey(this, "flow_data.json");
        root = survey.getRootQuestion();
        root.setAnswer(new Answer(Question.dummyOptions(), root));
        assertThat(root.getType(), is("ROOT"));
        flowLogicImplementation = new FlowLogicImplementation();
        nextQuestion = flowLogicImplementation.childFlow(root).question;
        assertThat(nextQuestion.getRawNumber(), is("1.1"));
    }

    @Test
    public void test_grid_flow() {
        Survey survey;
        Question root;
        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation();
        FlowLogic.FlowData nextFlowData;
        ArrayList<Option> options;

        survey = DataHelpers.getSurvey(this, "grid_question.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));
        flowLogicImplementation.setCurrent(root, FlowLogic.FlowData.FlowUIType.DEFAULT);

        nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextFlowData.question.getRawNumber(), is("2.1.7"));
        assertThat(nextFlowData.flowType, is(FlowLogic.FlowData.FlowUIType.DEFAULT));

        nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextFlowData.question.getRawNumber(), is("2.1.7"));
        assertThat(nextFlowData.flowType, is(FlowLogic.FlowData.FlowUIType.GRID));

        flowLogicImplementation.moveToIndexInChild(0);
        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "2")
        );
        flowLogicImplementation.update(options);

        nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextFlowData.question.getRawNumber(), is("2.1.7"));
        assertThat(nextFlowData.flowType, is(FlowLogic.FlowData.FlowUIType.GRID));

        flowLogicImplementation.finishCurrent();
        nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextFlowData.question.getRawNumber(), is("1.1"));
        assertThat(nextFlowData.flowType, is(FlowLogic.FlowData.FlowUIType.DEFAULT));
    }

    @Test
    public void test_loop_flow() {
        Survey survey;
        Question root;
        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation();
        FlowLogic.FlowData nextFlowData;
        ArrayList<Option> options;

        survey = DataHelpers.getSurvey(this, "loop_multiple_flow.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));
        flowLogicImplementation.setCurrent(root, FlowLogic.FlowData.FlowUIType.DEFAULT);

        nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextFlowData.question.getRawNumber(), is("2.1"));
        assertThat(nextFlowData.flowType, is(FlowLogic.FlowData.FlowUIType.DEFAULT));

        nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextFlowData.question.getRawNumber(), is("2.1.7"));
        assertThat(nextFlowData.flowType, is(FlowLogic.FlowData.FlowUIType.DEFAULT));

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "2")
        );
        flowLogicImplementation.update(options);

        nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextFlowData.question.getRawNumber(), is("2.1"));
        assertThat(nextFlowData.flowType, is(FlowLogic.FlowData.FlowUIType.LOOP));
    }

    @Test
    public void test_loop_options_flow() {
        Survey survey;
        Question root;
        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation();
        FlowLogic.FlowData nextFlowData;
        ArrayList<Option> options;

        survey = DataHelpers.getSurvey(this, "loop_options_flow.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));
        flowLogicImplementation.setCurrent(root, FlowLogic.FlowData.FlowUIType.DEFAULT);

        nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextFlowData.question.getRawNumber(), is("2"));
        assertThat(nextFlowData.flowType, is(FlowLogic.FlowData.FlowUIType.DEFAULT));

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "2")
        );
        flowLogicImplementation.update(options);

        nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextFlowData.question.getRawNumber(), is("2"));
        assertThat(nextFlowData.flowType, is(FlowLogic.FlowData.FlowUIType.LOOP));

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "2")
        );
        flowLogicImplementation.update(options);

        nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextFlowData.flowType, is(FlowLogic.FlowData.FlowUIType.END));
    }
}
