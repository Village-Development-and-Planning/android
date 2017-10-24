package com.puthuvaazhvu.mapping.other.flow;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.ModalHelpers;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.deep_copy.DeepCopy;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswerData;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.BackFlowImplementation;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.IBackFlow;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by muthuveerappans on 10/24/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class BackFlowTest {

    private Survey survey;
    private BackFlowImplementation backFlowImplementation;
    private Question root;

    @Before
    public void setup() {
        survey = ModalHelpers.getSurvey(this);
        root = survey.getQuestionList().get(0);
        backFlowImplementation = new BackFlowImplementation();
    }

    @Test
    public void test_getPreviousQuestion_method() {
        Question question = root.getChildren().get(1);

        assertThat(question.getRawNumber(), is("2"));

        // mock answer 2
        ArrayList<Question> children = addMockAnswer(question);

        Question question_2_1 = children.get(1);

        assertThat(question_2_1.getRawNumber(), is("2.1"));

        // mock answer 2.1
        children = addMockAnswer(question_2_1);

        Question question_2_1_1 = children.get(0);

        assertThat(question_2_1_1.getRawNumber(), is("2.1.1"));

        IBackFlow.BackFlowData backFlowData = backFlowImplementation.getPreviousQuestion(question_2_1_1);

        assertThat(backFlowData.isError, is(false));
        assertThat(backFlowData.question.getRawNumber(), is("2.1"));
        assertThat(backFlowData.question.getAnswers().size(), is(0));

        backFlowData = backFlowImplementation.getPreviousQuestion(question_2_1);

        assertThat(backFlowData.isError, is(false));
        assertThat(backFlowData.question.getRawNumber(), is("2"));
        assertThat(backFlowData.question.getAnswers().size(), is(0));

        backFlowData = backFlowImplementation.getPreviousQuestion(question);

        assertThat(backFlowData.isError, is(true));
        assertThat(backFlowData.question.getRawNumber(), is("2"));
        assertThat(backFlowData.question.getAnswers().size(), is(0));
    }

    private ArrayList<Question> addMockAnswer(Question question) {
        ArrayList<Question> children = (ArrayList<Question>) DeepCopy.copy(question.getChildren());
        ArrayList<Option> options = new ArrayList<>();

        // add a random option
        if (question.getOptionList().isEmpty()) {
            InputAnswerData singleAnswerData = new InputAnswerData(question.getId(), question.getTextString(), "TEST");
            options.add(singleAnswerData.getOption().get(0));
        } else {
            options.add(question.getOptionList().get(0));
        }

        Answer answer = new Answer(options, children, question);
        question.setAnswer(answer);
        return children;
    }
}
