package com.puthuvaazhvu.mapping.modals;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.deep_copy.DeepCopy;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by muthuveerappans on 10/19/17.
 */

public class QuestionDataModalTest {

    private Survey survey;

    @Before
    public void setup() {
        survey = ModalHelpers.getSurvey(this);
    }

    @Test
    public void test_setAnswer_method_option_scope() {
        Question question = survey.getQuestionList().get(0).getChildren().get(1);

        assertThat(question.getRawNumber(), is("2"));

        ArrayList<Option> mockOptions = new ArrayList<>();
        mockOptions.add(question.getOptionList().get(0));

        Answer mockAnswer = new Answer(
                mockOptions,
                question
        );

        question.setAnswer(mockAnswer);

        assertThat(question.getAnswers().size(), is(1));

        mockOptions = new ArrayList<>();
        mockOptions.add(question.getOptionList().get(1));

        Answer mockAnswer_2 = new Answer(
                mockOptions,
                question
        );

        question.setAnswer(mockAnswer_2);

        assertThat(question.getAnswers().size(), is(2));

        // try adding the answered option
        mockOptions = new ArrayList<>();
        mockOptions.add(question.getOptionList().get(1));

        Answer mockAnswer_3 = new Answer(
                mockOptions,
                question
        );

        question.setAnswer(mockAnswer_3);

        assertThat(question.getAnswers().size(), is(2));
        assertThat(question.getCurrentAnswer().getOptions().get(0).getId(), is(question.getOptionList().get(1).getId()));
    }

    @Test
    public void test_setAnswer_once_scope() {
        Question question = survey.getQuestionList().get(0).getChildren().get(1).getChildren().get(0);

        assertThat(question.getRawNumber(), is("2.0"));

        ArrayList<Option> mockOptions = new ArrayList<>();
        mockOptions.add(new Option("1", null, null, null, null));

        Answer mockAnswer = new Answer(
                mockOptions,
                question
        );

        question.setAnswer(mockAnswer);

        mockOptions = new ArrayList<>();
        mockOptions.add(new Option("2", null, null, null, null));

        mockAnswer = new Answer(
                mockOptions,
                question
        );

        question.setAnswer(mockAnswer);

        assertThat(question.getCurrentAnswer().getOptions().get(0).getId(), is("2"));
        assertThat(question.getAnswers().size(), is(1));
    }

    @Test
    public void test_setAnswer_multiple_scope() {
        Question question = survey.getQuestionList().get(0).getChildren().get(1).getChildren().get(1);

        assertThat(question.getRawNumber(), is("2.1"));

        ArrayList<Option> mockOptions = new ArrayList<>();
        mockOptions.add(new Option("1", null, null, null, null));

        Answer mockAnswer = new Answer(
                mockOptions,
                question
        );

        question.setAnswer(mockAnswer);

        mockOptions = new ArrayList<>();
        mockOptions.add(new Option("2", null, null, null, null));

        mockAnswer = new Answer(
                mockOptions,
                question
        );
        question.setAnswer(mockAnswer);

        assertThat(question.getCurrentAnswer().getOptions().get(0).getId(), is("2"));
        assertThat(question.getAnswers().size(), is(2));
    }

    @Test
    public void test_toJson_method() {
        Question question = survey.getQuestionList().get(0).getChildren().get(1);

        assertThat(question.getRawNumber(), is("2"));

        ArrayList<Option> mockOptions = new ArrayList<>();
        mockOptions.add(new Option("1", null, null, null, "0"));

        Answer mockAnswer = new Answer(
                mockOptions,
                question
        );

        question.setAnswer(mockAnswer);

        Question child = mockAnswer.getChildren().get(1);

        assertNotSame(child.getAnswers(), question.getChildren().get(1).getAnswers());

        assertThat(child.getRawNumber(), is("2.1"));

        assertNotSame(child, question.getChildren().get(1));

        mockOptions = new ArrayList<>();
        mockOptions.add(new Option("1", null, null, null, "0"));
        mockOptions.add(new Option("2", null, null, null, "1"));
        mockOptions.add(new Option("3", null, null, null, "2"));

        mockAnswer = new Answer(
                mockOptions,
                child
        );

        child.setAnswer(mockAnswer);


        JsonObject jsonObject = question.getAsJson().getAsJsonObject();

        assertThat(jsonObject.get("answers").getAsJsonArray().size(), is(1));
        assertThat(jsonObject.get("children").getAsJsonArray().get(1).getAsJsonObject().get("answers").getAsJsonArray().size(), is(0));
        assertThat(jsonObject.get("answers").getAsJsonArray().get(0)
                        .getAsJsonObject().get("children").getAsJsonArray().get(1)
                        .getAsJsonObject().get("answers").getAsJsonArray().size()
                , is(1));
    }

}
