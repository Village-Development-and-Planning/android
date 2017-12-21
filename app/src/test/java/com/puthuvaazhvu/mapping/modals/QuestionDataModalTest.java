package com.puthuvaazhvu.mapping.modals;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.helpers.ModalHelpers;
import com.puthuvaazhvu.mapping.modals.flow.PreFlow;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import io.reactivex.Single;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;

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
    public void test_getQuestionRawNumberPrefix() {
        Question question = survey.getRootQuestion().getChildren().get(1).getChildren().get(1).getChildren().get(6);
        assertThat(question.getRawNumber(), is("2.1.7"));
        assertThat(question.getQuestionRawNumberPrefix(), is("2.1"));
    }

    @Test
    public void test_findQuestion_rawNumber() {
        Single<Survey> surveySingle = Survey.getSurveyInstanceWithUpdatedAnswers(ModalHelpers.getAnswersJson(this));
        Survey survey = surveySingle.blockingGet();

        assertThat(survey.getRootQuestion().getAnswers().size(), is(1));

        Question question = survey.getRootQuestion().getAnswers()
                .get(0).getChildren().get(1).getAnswers().get(0).getChildren().get(1)
                .getAnswers().get(0).getChildren().get(0);

        assertThat(question.getRawNumber(), is("2.1.1"));

        Question found = question.findAnsweredQuestion("2");

        assertThat(found.getRawNumber(), is("2"));

        question = survey.getRootQuestion().getAnswers()
                .get(0).getChildren().get(1).getAnswers().get(0).getChildren().get(1)
                .getAnswers().get(0).getChildren().get(1);

        assertThat(question.getRawNumber(), is("2.1.2"));

        found = question.findAnsweredQuestion("2.1");

        assertThat(found.getRawNumber(), is("2.1"));
    }

    @Test
    public void test_getPathOfCurrentQuestion() {

        /*
            R---
                |
                A1-
                | |
                1 2-- --
                    |   |
                    A3  A4
                        |
                        2.1
         */

        Question root = survey.getRootQuestion();

        Answer answer_1 = new Answer(null, root);
        root.setAnswer(answer_1);

        Answer answer_2 = new Answer(null, answer_1.getChildren().get(1));
        Answer answer_3 = new Answer(null, answer_1.getChildren().get(1));

        answer_1.getChildren().get(1).setAnswer(answer_2);
        answer_1.getChildren().get(1).setAnswer(answer_3);

        assertThat(answer_1.getChildren().get(1).getAnswers().size(), is(2));

        ArrayList<Integer> index = answer_3.getChildren().get(0).getPathOfCurrentQuestion();

        assertThat(index.size(), is(5));
        assertThat(index.get(0), is(0));
        assertThat(index.get(1), is(0));
        assertThat(index.get(2), is(1));
        assertThat(index.get(3), is(1));
        assertThat(index.get(4), is(0));

    }

    @Test
    public void test_populateAnswersInternal_method() {
        Survey survey = ModalHelpers.getSurvey(this, "survey_data_1.json");

        JsonObject answers = ModalHelpers.getAnswersJson(this);
        JsonObject questionWithAnswersJson = answers.get("question").getAsJsonObject();

        Question rootNode = survey.getRootQuestion();

        assertThat(rootNode.getType(), is("ROOT"));
        assertThat(questionWithAnswersJson.get("type").getAsString(), is("ROOT"));

        Question resultRoot = Question.populateAnswersInternal(rootNode, questionWithAnswersJson);

        assertThat(resultRoot.getAnswers().size(), is(1));
        assertThat(resultRoot.getAnswers().get(0).getOptions().size(), is(1));
        assertThat(resultRoot.getAnswers().get(0).getOptions().get(0).getType(), is("DUMMY"));
        assertThat(resultRoot.getAnswers().get(0).getChildren().size(), is(2));
    }

    @Test
    public void test_add_dynamicOptions() {
        Question root = survey.getRootQuestion();

        assertThat(root.getType(), is("ROOT"));

        Question result = root.findInTree(new Question.QuestionTreeSearchPredicate() {
            @Override
            public boolean evaluate(Question question) {
                return question.containsPreFlow(PreFlow.Tag.HABITATION_NAME);
            }
        });

        assertThat(result.getRawNumber(), is("2"));

        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("1", null, null, null, null));
        result.setOptionList(options);

        assertThat(result.getOptionList().get(0).getId(), is("1"));
    }

    @Test
    public void test_findInTree_method() {
        Question root = survey.getRootQuestion();

        assertThat(root.getType(), is("ROOT"));

        Question result = root.findInTree(new Question.QuestionTreeSearchPredicate() {
            @Override
            public boolean evaluate(Question question) {
                return question.containsPreFlow(PreFlow.Tag.HABITATION_NAME);
            }
        });

        assertThat(result.getRawNumber(), is("2"));

        result = root.findInTree(new Question.QuestionTreeSearchPredicate() {
            @Override
            public boolean evaluate(Question question) {
                return question.containsPreFlow(PreFlow.Tag.VILLAGE_NAME);
            }
        });

        assertThat(result.getRawNumber(), is("1.5"));
    }

    @Test
    public void test_containsPreFlow_method() {
        Question question = survey.getRootQuestion().getChildren().get(1);

        assertThat(question.getRawNumber(), is("2"));

        boolean result = question.containsPreFlow(PreFlow.Tag.HABITATION_NAME);

        assertThat(result, is(true));

        result = question.containsPreFlow(PreFlow.Tag.BLOCK_NAME);

        assertThat(result, is(false));

        question = survey.getRootQuestion().getChildren().get(0).getChildren().get(3);

        assertThat(question.getRawNumber(), is("1.5"));

        result = question.containsPreFlow(PreFlow.Tag.VILLAGE_NAME);

        assertThat(result, is(true));
    }

    @Test
    public void test_setAnswer_method_option_scope() {
        Question question = survey.getRootQuestion().getChildren().get(1);

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
        assertThat(question.getLatestAnswer().getOptions().get(0).getId(), is(question.getOptionList().get(1).getId()));
    }

    @Test
    public void test_setAnswer_once_scope() {
        Question question = survey.getRootQuestion().getChildren().get(1).getChildren().get(0);

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

        assertThat(question.getLatestAnswer().getOptions().get(0).getId(), is("2"));
        assertThat(question.getAnswers().size(), is(1));
    }

    @Test
    public void test_setAnswer_multiple_scope() {
        Question question = survey.getRootQuestion().getChildren().get(1).getChildren().get(1);

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

        assertThat(question.getLatestAnswer().getOptions().get(0).getId(), is("2"));
        assertThat(question.getAnswers().size(), is(2));
    }

    @Test
    public void test_toJson_method() {
        Question question = survey.getRootQuestion().getChildren().get(1);

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


        JsonObject jsonObject = question.getAsJson().getAsJsonObject().get("question").getAsJsonObject();

        assertThat(jsonObject.get("answers").getAsJsonArray().size(), is(1));
        assertThat(jsonObject.get("children").getAsJsonArray().get(1).getAsJsonObject().get("question").getAsJsonObject().get("answers").getAsJsonArray().size(), is(0));
        assertThat(jsonObject.get("answers").getAsJsonArray().get(0)
                        .getAsJsonObject().get("children").getAsJsonArray().get(1)
                        .getAsJsonObject().get("question").getAsJsonObject().get("answers").getAsJsonArray().size()
                , is(1));
    }

}
