package com.puthuvaazhvu.mapping.flow;

import android.content.Context;
import android.content.SharedPreferences;

import com.puthuvaazhvu.mapping.TestUtils;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogicImplementation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static junit.framework.Assert.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;

/**
 * Created by muthuveerappans on 10/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class FlowImplementationTest {

    private SharedPreferences sharedPrefs;
    private Context context;

    @Before
    public void before() throws Exception {
        this.sharedPrefs = Mockito.mock(SharedPreferences.class);
        this.context = Mockito.mock(Context.class);
        Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
    }

    private void addDummyAnswersToChildren(Question node) {
        Answer answer = new Answer(QuestionUtils.generateQuestionWithDummyOptions(), node, System.currentTimeMillis());
        node.addAnswer(answer);

        for (Question child : node.getCurrentAnswer().getChildren()) {
            addDummyAnswersToChildren(child);
        }
    }

    private Question moveTo(String path, Question question) {
        return QuestionUtils.moveToQuestionUsingPath(path, question);
    }

    @Test
    public void test_update() {
        Survey survey = TestUtils.getSurvey(this, "flow_data.json");
        Question root = survey.getRootQuestion();

        assertThat(survey.getId(), is("5a5ef00e0526a120e341d7da"));

        addDummyAnswersToChildren(root);

        FlowLogic flowLogic = new FlowLogicImplementation(root, sharedPrefs);
        Question question = moveTo("0,0,1,0", root);
        assertThat(question.getRawNumber(), is("1.2"));
        flowLogic.setCurrent(question);

        ArrayList<Option> options = new ArrayList<>();
        options.add(
                new Option("TEST_DATA",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "")
        );

        flowLogic.update(options);

        assertThat(flowLogic.getCurrent().getQuestion().getAnswers().size(), is(1));
//        assertThat(QuestionUtils.getLastAnswer(flowLogic.getCurrent().getQuestion().question)
//                .getOptions().read(0).getType(), is("TEST_DATA"));
        assertThat(flowLogic.getCurrent().getQuestion().getCurrentAnswer()
                .getOptions().get(0).getType(), is("TEST_DATA"));

        survey = TestUtils.getSurvey(this, "survey_data_test_unit.json");
        root = survey.getRootQuestion();

        assertThat(survey.getId(), is("59eadef25b146a66ce5aff0e"));

        question = root.getChildren().get(1);

        assertThat(question.getRawNumber(), is("2"));

        flowLogic.setCurrent(question);

        options = new ArrayList<>();
        options.add(
                new Option("TEST_DATA",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "0")
        );

        flowLogic.update(options);

        options = new ArrayList<>();
        options.add(
                new Option("TEST_DATA",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "0")
        );

        flowLogic.update(options);

        flowLogic.update(options);

        options = new ArrayList<>();
        options.add(
                new Option("TEST_DATA",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "1")
        );

        flowLogic.update(options);

        assertThat(question.getAnswers().size(), is(2));
        assertThat(question.getAnswers().get(0).getOptions().get(0).getId(), is("TEST_DATA"));
        assertThat(question.getAnswers().get(1).getOptions().get(0).getId(), is("TEST_DATA"));

        options = new ArrayList<>();
        options.add(
                new Option("1",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "1")
        );

        flowLogic.update(options);
        assertThat(question.getAnswers().get(1).getOptions().get(0).getId(), is("1"));
    }

    @Test
    public void test_finishCurrent() {
        Survey survey = TestUtils.getSurvey(this, "flow_data.json");
        Question root = survey.getRootQuestion();

        assertThat(survey.getId(), is("5a5ef00e0526a120e341d7da"));

        addDummyAnswersToChildren(root);

        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation(root, sharedPrefs);
        Question question = moveTo("0,0,1,0", root);
        assertThat(question.getRawNumber(), is("1.2"));
        flowLogicImplementation.setCurrent(question);

        Question q = flowLogicImplementation.finishCurrent().getQuestion();

        assertThat(q.getRawNumber(), is("1.3"));
    }

    @Test
    public void test_getNext() {
        Survey survey = TestUtils.getSurvey(this, "flow_data.json");
        Question root = survey.getRootQuestion();

        assertThat(survey.getId(), is("5a5ef00e0526a120e341d7da"));

        addDummyAnswersToChildren(root);

        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation(root, sharedPrefs);

        // normal flow
        flowLogicImplementation.getNext();
        assertThat(flowLogicImplementation.getCurrent().getQuestion().getRawNumber(), is("1.1"));

        // test shown together
        survey = TestUtils.getSurvey(this, "shown_together_question.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));

        flowLogicImplementation.setCurrent(root);
        Question nextQuestion = flowLogicImplementation.getNext().getQuestion();

        assertThat(nextQuestion.getAnswers().size(), is(1));

        // test grid question
        survey = TestUtils.getSurvey(this, "grid_question.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));

        flowLogicImplementation.setCurrent(root);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getAnswers().size(), is(1));

        flowLogicImplementation.moveToIndexInChild(0);
        assertThat(flowLogicImplementation.getCurrent().getQuestion().getRawNumber(), is("2.1.7.3"));

        // test skip pattern
        survey = TestUtils.getSurvey(this, "multiple_options_skip.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));

        flowLogicImplementation = new FlowLogicImplementation(root, sharedPrefs);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();

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
        assertThat(flowLogicImplementation.getCurrent().getQuestion().getAnswers().size(), is(1));

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1.6.5.1"));
        assertThat(nextQuestion.getAnswers().size(), is(1));

        // test skip false
        survey = TestUtils.getSurvey(this, "multiple_options_skip.json");
        root = survey.getRootQuestion();
        flowLogicImplementation = new FlowLogicImplementation(root, sharedPrefs);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();

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
        assertThat(flowLogicImplementation.getCurrent().getQuestion().getAnswers().size(), is(1));

        FlowLogic.FlowData flowData = flowLogicImplementation.getNext();
        assertThat(flowData, is(nullValue()));
    }

    @Test
    public void test_moveToIndexInChild() {
        Survey survey = TestUtils.getSurvey(this, "flow_data.json");
        Question root = survey.getRootQuestion();

        assertThat(survey.getId(), is("5a5ef00e0526a120e341d7da"));

        addDummyAnswersToChildren(root);

        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation(root, sharedPrefs);
        flowLogicImplementation.moveToIndexInChild(13);

        assertThat(flowLogicImplementation.getCurrent().getQuestion().getRawNumber(), is("2"));
        assertThat(flowLogicImplementation.getCurrent().getQuestion().getAnswers().size(), is(1));
    }

    @Test
    public void test_getPrevious() {
        Question nextQuestion;

        Survey survey = TestUtils.getSurvey(this, "back_test.json");
        Question root = survey.getRootQuestion();
        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation(root, sharedPrefs);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1.7"));

        nextQuestion = flowLogicImplementation.getPrevious().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1.7"));

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1.7"));

        nextQuestion = flowLogicImplementation.moveToIndexInChild(0).getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1.7.3"));

        nextQuestion = flowLogicImplementation.getPrevious().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1.7"));

        survey = TestUtils.getSurvey(this, "household_new.json");
        root = survey.getRootQuestion();
        flowLogicImplementation = new FlowLogicImplementation(root, sharedPrefs);

        for (int i = 1; i <= 13; i++) {
            nextQuestion = flowLogicImplementation.getNext().getQuestion();
        }

        assertThat(nextQuestion.getRawNumber(), is("2.1.1"));

        Question prevQuestion = flowLogicImplementation.getPrevious().getQuestion();

        assertThat(prevQuestion.getRawNumber(), is("1.12"));

        nextQuestion = flowLogicImplementation.getNext().getQuestion();

        assertThat(nextQuestion.getRawNumber(), is("2.1.1"));
    }

    @Test
    public void test_grid_flow() {
        Survey survey;
        Question root;
        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation();
        Question nextQuestion;
        ArrayList<Option> options;

        survey = TestUtils.getSurvey(this, "grid_question.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));
        flowLogicImplementation.setCurrent(root);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1.7"));

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1.7"));

        assertThat(flowLogicImplementation.getCurrent().getQuestion().getRawNumber(), is("2.1.7"));

        flowLogicImplementation.moveToIndexInChild(18);
        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "2")
        );
        flowLogicImplementation.update(options);

        assertThat(flowLogicImplementation.getCurrent().getQuestion().getRawNumber(), is("2.1.7.21"));

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1.7"));

        assertThat(flowLogicImplementation.getCurrent().getQuestion().getRawNumber(), is("2.1.7"));

        nextQuestion = flowLogicImplementation.finishCurrent().getQuestion();
        //nextFlowData = flowLogicImplementation.getNext();
        assertThat(nextQuestion.getRawNumber(), is("1.1"));
    }

    @Test
    public void test_loop_flow() {
        Survey survey;
        Question root;
        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation();
        Question nextQuestion;
        ArrayList<Option> options;

        survey = TestUtils.getSurvey(this, "loop_multiple_flow.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));
        flowLogicImplementation.setCurrent(root);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1"));

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "2")
        );
        flowLogicImplementation.update(options);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1.7"));

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "2")
        );
        flowLogicImplementation.update(options);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2.1"));
    }

    @Test
    public void test_loop_options_flow() {
        Survey survey;
        Question root;
        FlowLogicImplementation flowLogicImplementation = new FlowLogicImplementation();
        Question nextQuestion;
        ArrayList<Option> options;

        survey = TestUtils.getSurvey(this, "loop_options_flow.json");
        root = survey.getRootQuestion();
        assertThat(root.getType(), is("ROOT"));
        flowLogicImplementation.setCurrent(root);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2"));

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "1")
        );
        flowLogicImplementation.update(options);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();
        assertThat(nextQuestion.getRawNumber(), is("2"));

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "1")
        );
        flowLogicImplementation.update(options);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "0")
        );
        flowLogicImplementation.update(options);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "1")
        );
        flowLogicImplementation.update(options);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "0")
        );
        flowLogicImplementation.update(options);

        nextQuestion = flowLogicImplementation.getNext().getQuestion();

        options = new ArrayList<>();
        options.add(
                new Option("",
                        "TEST_DATA",
                        new Text("", "TEST", "TEST", ""),
                        "",
                        "2")
        );
        flowLogicImplementation.update(options);

        FlowLogic.FlowData flowData = flowLogicImplementation.getNext();
        assertThat(flowData, is(nullValue()));
    }
}
