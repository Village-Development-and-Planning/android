package org.ptracking.vdp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ptracking.vdp.Helpers.DataTraversing;
import org.ptracking.vdp.Helpers.ReadTestFile;
import org.ptracking.vdp.modals.Answer;
import org.ptracking.vdp.modals.FlowPattern;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.modals.Survey;
import org.ptracking.vdp.modals.deserialization.SurveyGsonAdapter;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by muthuveerappans on 19/02/18.
 */

public class SurveyGsonAdapterTest {

    private Gson gson;

    @Before
    public void setUp() throws Exception {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());

        gson = gsonBuilder.create();
    }

    @Test
    public void validity() throws Exception {
        Survey survey = ReadTestFile.getTestSurvey(this, "testing_validation.json");
        assertThat(
                "Check UI is none for 2.0.1",
                survey.getQuestion().getChildren().get(0).getFlowPattern().getQuestionFlow().getUiMode(),
                is(FlowPattern.QuestionFlow.UI.NONE)
        );
    }

    @Test
    public void answersJsonSerialization() throws Exception {
        Survey survey = ReadTestFile.getTestMappingSurvey(this);
        addAnswersToTreeFromQuestion(survey.getQuestion());

        String json = gson.toJson(survey, Survey.class);

        assertThat(
                "Check Not null json",
                json,
                is(notNullValue())
        );
    }

    @Test
    public void householdTest() throws Exception {
        Survey survey = ReadTestFile.getTestHouseholdSurvey(this);

        assertThat("Check survey ID", survey.getId(), is("5a6cb2a371cf4b33051c6509"));

        Question root = survey.getQuestion();

        assertThat(
                "Check for image data at q.num 2.5.4.2",
                DataTraversing.findQuestion("2.5.4.2", root).getOptions().get(0).getImageData(),
                is("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")
        );
    }

    @Test
    public void mappingTest() throws Exception {
        Survey survey = ReadTestFile.getTestMappingSurvey(this);

        assertThat("Check for not null value", survey, is(notNullValue()));
        assertThat("Survey ID check", survey.getId(), is("5a7886d9fdb3a526f48afe25"));

        Question root = survey.getQuestion();

        assertThat("Root question Children count check", root.getChildren().size(), is(5));

        assertThat(
                "Root Question flow UI mode check",
                root.getFlowPattern().getQuestionFlow().getUiMode(),
                is(FlowPattern.QuestionFlow.UI.NONE)
        );

        assertThat("Check root has empty position", root.getPosition(), is(""));

        assertThat("Check number q.num : 1.3",
                DataTraversing.findQuestion("1.3", root).getNumber(), is("1.3"));

        assertThat(
                "Check pre-flow q.num : 2",
                DataTraversing.findQuestion("2", root).getFlowPattern().getPreFlow().getFill().get(0),
                is("HABITATION_NAME")
        );

        assertThat(
                "Check question-flow UI q.num : 2",
                DataTraversing.findQuestion("2", root).getFlowPattern().getQuestionFlow().getUiMode(),
                is(FlowPattern.QuestionFlow.UI.SINGLE_CHOICE)
        );

        assertThat(
                "Check question-flow Validation q.num : 2",
                DataTraversing.findQuestion("2", root).getFlowPattern().getQuestionFlow().getValidation(),
                is(FlowPattern.QuestionFlow.Validation.NONE)
        );

        assertThat(
                "Check question-flow back q.num : 2",
                DataTraversing.findQuestion("2", root).getFlowPattern().getQuestionFlow().isBack(),
                is(false)
        );

        assertThat(
                "Check answer-flow q.num : 2",
                DataTraversing.findQuestion("2", root).getFlowPattern().getAnswerFlow().getMode(),
                is(FlowPattern.AnswerFlow.Modes.OPTION)
        );

        assertThat(
                "Check child-flow q.num : 2",
                DataTraversing.findQuestion("2", root).getFlowPattern().getChildFlow().getStrategy(),
                is(FlowPattern.ChildFlow.Strategy.CASCADE)
        );

        assertThat(
                "Check post-flow q.num : 2",
                DataTraversing.findQuestion("2", root).getFlowPattern().getPostFlow().getTags().size(),
                is(0)
        );

        assertThat(
                "Check exit-flow q.num : 2",
                DataTraversing.findQuestion("2", root).getFlowPattern().getExitFlow().getStrategy(),
                is(FlowPattern.ExitFlow.Strategy.LOOP)
        );

        assertThat(
                "Check skip unless q.num : 2.1.5.1",
                DataTraversing.findQuestion("2.1.5.1", root).getFlowPattern().getPreFlow().getSkipUnless()
                        .getQuestionNumber(),
                is("2.1.5")
        );

        assertThat(
                "Check option count q.num : 2.1.5.1",
                DataTraversing.findQuestion("2.1.5.1", root).getOptions().size(),
                is(5)
        );

        assertThat(
                "Check option position q.num : 2.1.5.1",
                DataTraversing.findQuestion("2.1.5.1", root).getOptions().get(1).getPosition(),
                is("2")
        );

        assertThat(
                "Check option text english q.num : 2.1.5.1",
                DataTraversing.findQuestion("2.1.5.1", root).getOptions().get(1).getText().getEnglish(),
                is("Gravel")
        );
    }

    private void addAnswersToTreeFromQuestion(Question node) {
        addAnswer(node);
        for (Question c : node.getCurrentAnswer().getChildren()) {
            addAnswersToTreeFromQuestion(c);
        }
    }

    private void addAnswer(Question question) {
        Answer answer2 = Answer.createDummyAnswer(question);
        answer2.setDummy(false);
        question.addAnswer(answer2);
    }
}
