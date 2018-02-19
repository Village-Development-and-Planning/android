package com.puthuvaazhvu.mapping;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;

/**
 * Created by muthuveerappans on 10/19/17.
 */

public class QuestionUtilsTest {

    private Survey survey;

    @Before
    public void setup() {
        survey = TestUtils.getSurvey(this);
    }

    @Test
    public void test_getQuestionRawNumberPrefix() {
        Question question = survey.getQuestion().getChildren().get(1).getChildren().get(1).getChildren().get(6);
        assertThat(question.getNumber(), is("2.1.7"));
        assertThat(QuestionUtils.getQuestionParentNumber(question), is("2.1"));
    }

    @Test
    public void test_findQuestion_rawNumber() {
        io.reactivex.Observable<Survey> surveySingle = SurveyUtils.getSurveyWithUpdatedAnswers(TestUtils.getAnswersJson(this));
        Survey survey = surveySingle.blockingFirst();

        assertThat(survey.getQuestion().getAnswers().size(), is(1));

        Question question = survey.getQuestion().getAnswers()
                .get(0).getChildren().get(1).getAnswers().get(0).getChildren().get(1)
                .getAnswers().get(0).getChildren().get(0);

        assertThat(question.getNumber(), is("2.1.1"));

        Question found = QuestionUtils.findQuestionFrom(question, "2", true);

        assertThat(found.getNumber(), is("2"));

        question = survey.getQuestion().getAnswers()
                .get(0).getChildren().get(1).getAnswers().get(0).getChildren().get(1)
                .getAnswers().get(0).getChildren().get(1);

        assertThat(question.getNumber(), is("2.1.2"));

        found = QuestionUtils.findQuestionFrom(question, "2.1", true);

        assertThat(found.getNumber(), is("2.1"));
    }

    @Test
    public void test_getPathOfCurrentQuestion() {
        Survey survey = TestUtils.getSurvey(this, "answers_data_1.json");
        JsonObject questionJson =
                TestUtils.getJson(this, "answers_data_1.json").get("question").getAsJsonObject();
        QuestionUtils.populateAnswersFromJson(survey.getQuestion(), questionJson);

        assertThat(survey.getId(), is("5a08957bad04f82a15a0e974"));
        assertThat(survey.getQuestion().getAnswers().size(), is(1));

        Question question = survey.getQuestion().getAnswers().get(0)
                .getChildren().get(1).getAnswers().get(1).getChildren().get(1).getAnswers().get(0)
                .getChildren().get(0);

        assertThat(question.getNumber(), is("2.1.1"));

        ArrayList<Integer> path = QuestionUtils.getPathOfQuestion(question);
        assertThat(path.toString(), is("[0, 0, 1, 1, 1, 0, 0]"));

        Question q = QuestionUtils.moveToQuestionUsingPath("0,0,1,1,1,0,0", survey.getQuestion());
        assertThat(q.getNumber(), is("2.1.1"));
    }

    @Test
    public void test_populateAnswersInternal_method() {
        Survey survey = TestUtils.getSurvey(this, "survey_data_1.json");

        JsonObject answers = TestUtils.getAnswersJson(this);
        JsonObject questionWithAnswersJson = answers.get("question").getAsJsonObject();

        Question rootNode = survey.getQuestion();

        assertThat(rootNode.getType(), is("ROOT"));
        assertThat(questionWithAnswersJson.get("type").getAsString(), is("ROOT"));

        Question resultRoot = QuestionUtils.populateAnswersFromJson(rootNode, questionWithAnswersJson);

        assertThat(resultRoot.getAnswers().size(), is(1));
        assertThat(resultRoot.getAnswers().get(0).getOptions().size(), is(1));
        assertThat(resultRoot.getAnswers().get(0).getOptions().get(0).getType(), is("DUMMY"));
        assertThat(resultRoot.getAnswers().get(0).getChildren().size(), is(2));
    }

    @Test
    public void test_toJson_method() {
        Question question = survey.getQuestion().getChildren().get(1);

        assertThat(question.getNumber(), is("2"));

        ArrayList<Option> mockOptions = new ArrayList<>();
        mockOptions.add(new Option("1", null, null, null, "0"));

        Answer mockAnswer = new Answer(
                mockOptions,
                question
        );

        question.addAnswer(mockAnswer);

        Question child = mockAnswer.getChildren().get(1);

        assertNotSame(child.getAnswers(), question.getChildren().get(1).getAnswers());

        assertThat(child.getNumber(), is("2.1"));

        assertNotSame(child, question.getChildren().get(1));

        mockOptions = new ArrayList<>();
        mockOptions.add(new Option("1", null, null, null, "0"));
        mockOptions.add(new Option("2", null, null, null, "1"));
        mockOptions.add(new Option("3", null, null, null, "2"));

        mockAnswer = new Answer(
                mockOptions,
                child
        );

        child.addAnswer(mockAnswer);


        JsonObject jsonObject = question.getAsJson().getAsJsonObject().get("question").getAsJsonObject();

        assertThat(jsonObject.get("answers").getAsJsonArray().size(), is(1));
        assertThat(jsonObject.get("children").getAsJsonArray().get(1).getAsJsonObject().get("question").getAsJsonObject().get("answers").getAsJsonArray().size(), is(0));
        assertThat(jsonObject.get("answers").getAsJsonArray().get(0)
                        .getAsJsonObject().get("children").getAsJsonArray().get(1)
                        .getAsJsonObject().get("question").getAsJsonObject().get("answers").getAsJsonArray().size()
                , is(1));
    }

}
