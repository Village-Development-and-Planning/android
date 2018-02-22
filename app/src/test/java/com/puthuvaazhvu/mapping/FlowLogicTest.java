package com.puthuvaazhvu.mapping;

import android.content.Context;
import android.content.SharedPreferences;

import com.puthuvaazhvu.mapping.Helpers.DataTraversing;
import com.puthuvaazhvu.mapping.Helpers.ReadTestFile;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogicImplementation;
import com.puthuvaazhvu.mapping.views.fragments.question.GridQuestionsFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static junit.framework.Assert.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

/**
 * Created by muthuveerappans on 20/02/18.
 */

@RunWith(MockitoJUnitRunner.class)
public class FlowLogicTest {

    private SharedPreferences sharedPrefs;
    private Context context;

    private FlowLogic flowLogicImplementation;

    @Before
    public void before() throws Exception {
        this.sharedPrefs = Mockito.mock(SharedPreferences.class);
        this.context = Mockito.mock(Context.class);
        Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);

        flowLogicImplementation = new FlowLogicImplementation(null, sharedPrefs);
    }

    @Test
    public void answerValidationTime() throws Exception {
        FlowLogic.FlowData flowData;

        Survey dummySurvey = ReadTestFile.getTestSurvey(this, "testing_answers_start_time_end_time.json");
        Question root = dummySurvey.getQuestion();

        flowLogicImplementation.setCurrent(root);

        flowLogicImplementation.getNext();
        flowLogicImplementation.update(sampleOption("1"));

        flowLogicImplementation.getNext();
        flowLogicImplementation.update(sampleOption("TEST"));

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "END",
                flowData,
                is(nullValue())
        );

        long startTime = root.getCurrentAnswer().getStartTimeStamp();
        long endTime = root.getCurrentAnswer().getExitTimestamp();

        Question q2 = root.getCurrentAnswer().getChildren().get(0);
        long loopStartTime = q2.getCurrentAnswer().getStartTimeStamp();
        long loopEndTime = q2.getCurrentAnswer().getExitTimestamp();

        assertThat(
                "Check if the start time and end time are different for LOOP question",
                (int) (loopEndTime - loopStartTime),
                is(greaterThan(0))
        );

        assertThat(
                "Check if the start time and end time are different for survey",
                (int) (endTime - startTime),
                is(greaterThan(0))
        );
    }

    @Test
    public void validation() throws Exception {
        FlowLogic.FlowData flowData;

        Survey dummySurvey = ReadTestFile.getTestSurvey(this, "testing_validation.json");
        Question root = dummySurvey.getQuestion();

        flowLogicImplementation.setCurrent(root);

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Test skip to next question when UI error",
                flowData.getQuestion().getNumber(),
                is("2.1")
        );
    }

    @Test
    public void update() throws Exception {
        Survey dummySurvey = ReadTestFile.getTestSurvey(this, "testing_loop_options.json");
        Question root = dummySurvey.getQuestion();

        Question q2 = DataTraversing.findQuestion("2", root);

        flowLogicImplementation.setCurrent(q2);

        for (int i = 0; i < q2.getOptions().size(); i++) {
            addAnswer(q2);
            q2.getCurrentAnswer().setDummy(true);
            addOption(q2.getOptions().get(i).getPosition(), q2);
        }

        for (int i = 0; i < q2.getOptions().size(); i++) {
            ArrayList<Option> options = new ArrayList<>();
            options.add(new Option(null, null, "" + i));
            flowLogicImplementation.update(options);

            assertThat(
                    "Test if the options are updated with dummy false",
                    flowLogicImplementation.getCurrent().getQuestion().getAnswers().get(i).isDummy(),
                    is(false)
            );

            assertThat(
                    "Test if the options are properly updated",
                    flowLogicImplementation.getCurrent().getQuestion().getAnswers().get(i).getLoggedOptions().get(0)
                            .getPosition(),
                    is("" + i)
            );
        }

        Question q2_0_1 = DataTraversing.findQuestion("2.0.1", root);

        addAnswer(q2_0_1);

        flowLogicImplementation.setCurrent(q2_0_1);

        for (int i = 0; i < 10; i++) {
            ArrayList<Option> options = new ArrayList<>();
            options.add(new Option(null, null, "" + i));
            flowLogicImplementation.update(options);
        }

        assertThat(
                "Test if the options are updated with dummy false",
                flowLogicImplementation.getCurrent().getQuestion().getAnswers().get(0).isDummy(),
                is(false)
        );

        assertThat(
                "Test answers scope single",
                flowLogicImplementation.getCurrent().getQuestion().getAnswers().size(),
                is(1)
        );

        assertThat(
                "Test answers scope single with overridden value",
                flowLogicImplementation.getCurrent().getQuestion().getAnswers().get(0).getLoggedOptions().get(0).getPosition(),
                is("9")
        );
    }

    @Test
    public void backFlow() throws Exception {
        FlowLogic.FlowData flowData;

        Survey dummySurvey = ReadTestFile.getTestSurvey(this, "testing_back_logic.json");

        Question root = dummySurvey.getQuestion();

        addAnswer(root);

        Question q1_1 = root.getCurrentAnswer().getChildren().get(0);
        addAnswer(q1_1);

        flowLogicImplementation.setCurrent(q1_1);

        flowData = flowLogicImplementation.getNext();

        addAnswer(flowData.getQuestion());
        addOption("1", flowData.getQuestion());

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Check creation of dummy answers",
                flowData.getQuestion().getCurrentAnswer().isDummy(),
                is(true)
        );

        flowData = flowLogicImplementation.getPrevious();

        assertThat(
                "Check question 1.12 is shown",
                flowData.getQuestion().getNumber(),
                is("1.12")
        );

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Question 2 is current",
                flowData.getQuestion().getNumber(),
                is("2")
        );
    }

    @Test
    public void skipPattern() throws Exception {
        FlowLogic.FlowData flowData;

        Survey dummySurvey = ReadTestFile.getTestSurvey(this, "testing_skip_pattern.json");

        Question root = dummySurvey.getQuestion();

        addAnswer(root);

        Question q2_1_6_1 = root.getCurrentAnswer().getChildren().get(0);
        addAnswer(q2_1_6_1);
        addOption("0", q2_1_6_1);

        flowLogicImplementation.setCurrent(q2_1_6_1);

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Skips all the questions",
                flowData,
                is(nullValue())
        );

        addAnswer(q2_1_6_1);
        addOption("1", q2_1_6_1);

        Question q2_1_6_1_1 = flowLogicImplementation.getNext().getQuestion();
        addAnswer(q2_1_6_1_1);
        addOption("99", q2_1_6_1_1);

        addAnswer(flowLogicImplementation.getNext().getQuestion());
        addAnswer(flowLogicImplementation.getNext().getQuestion());

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Skips questions and moves to parent",
                flowData,
                is(nullValue())
        );
    }

    @Test
    public void gridFlowType() throws Exception {
        FlowLogic.FlowData flowData;

        Survey dummySurvey = ReadTestFile.getTestSurvey(this, "testing_grid.json");

        Question root = dummySurvey.getQuestion();

        addAnswer(root);

        Question q2_1_6 = root.getCurrentAnswer().getChildren().get(0);
        addAnswer(q2_1_6);

        flowLogicImplementation.setCurrent(q2_1_6);

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Shows GRID UI for 2.1.6",
                flowData.getFragment(),
                instanceOf(GridQuestionsFragment.class)
        );

        flowData = flowLogicImplementation.moveToIndexInChild(0);

        assertThat(
                "Check moved to child 2.1.6.12",
                flowData.getQuestion().getNumber(),
                is("2.1.6.12")
        );

        addAnswer(flowData.getQuestion());

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Shows GRID UI for 2.1.6",
                flowData.getQuestion().getNumber(),
                is("2.1.6")
        );

        flowData = flowLogicImplementation.finishCurrentAndGetNext();

        assertThat(
                "End",
                flowData,
                is(nullValue())
        );
    }

    @Test
    public void loopMultipleType() throws Exception {
        FlowLogic.FlowData flowData;

        Survey dummySurvey = ReadTestFile.getTestSurvey(this, "testing_loop_multiple.json");

        Question root = dummySurvey.getQuestion();

        addAnswer(root);

        Question q2_1 = root.getCurrentAnswer().getChildren().get(0);

        flowLogicImplementation.setCurrent(q2_1);

        addAnswer(q2_1);

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Next cascade question is 2.1.1",
                flowData.getQuestion().getNumber(),
                is("2.1.1")
        );

        addAnswer(flowData.getQuestion());

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Next cascade question - exit flow - loop multiple",
                flowData.getQuestion().getNumber(),
                is("2.1")
        );

        addAnswer(flowData.getQuestion());

        addAnswer(flowLogicImplementation.getNext().getQuestion());

        flowLogicImplementation.getNext();

        flowData = flowLogicImplementation.finishCurrentAndGetNext();

        assertThat(
                "End",
                flowData,
                is(nullValue())
        );
    }

    @Test
    public void loopOptionsType() throws Exception {
        FlowLogic.FlowData flowData;

        Survey dummySurvey = ReadTestFile.getTestSurvey(this, "testing_loop_options.json");

        Question root = dummySurvey.getQuestion();

        addAnswer(root);

        Question q2 = root.getCurrentAnswer().getChildren().get(0);

        flowLogicImplementation.setCurrent(q2);

        addAnswer(q2);

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Next question is 2.0.1 for cascade",
                flowData.getQuestion().getNumber(),
                is("2.0.1")
        );

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Next question is 2.1 for cascade",
                flowData.getQuestion().getNumber(),
                is("2.1")
        );

        addAnswer(flowData.getQuestion());

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "Next question is again 2 for exit strategy LOOP OPTIONS",
                flowData.getQuestion().getNumber(),
                is("2")
        );

        q2.getAnswers().clear();

        addAnswer(q2);
        addOption("0", q2);
        addAnswer(q2);
        addOption("1", q2);

        Question q2_1 = q2.getCurrentAnswer().getChildren().get(1);
        addAnswer(q2_1);

        flowLogicImplementation.setCurrent(q2_1);

        flowData = flowLogicImplementation.getNext();

        assertThat(
                "LOOP OPTIONS complete",
                flowData,
                is(nullValue())
        );
    }

    private ArrayList<Option> sampleOption(String position) {
        Option option = new Option(null, null, position);
        ArrayList<Option> options = new ArrayList<>();
        options.add(option);
        return options;
    }

    private void addOption(String position, Question question) {
        Option option = new Option(null, null, position);
        ArrayList<Option> options = new ArrayList<>();
        options.add(option);
        question.getCurrentAnswer().setLoggedOptions(options);
    }

    private void addAnswer(Question question) {
        Answer answer2 = Answer.createDummyAnswer(question);
        answer2.setDummy(false);
        question.addAnswer(answer2);
    }
}
