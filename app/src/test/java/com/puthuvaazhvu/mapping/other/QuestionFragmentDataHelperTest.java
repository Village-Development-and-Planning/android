package com.puthuvaazhvu.mapping.other;

import com.puthuvaazhvu.mapping.modals.Flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.SurveyDataModelTest;
import com.puthuvaazhvu.mapping.views.activities.Presenter;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Option;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.Answer;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswer;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridData;
import com.puthuvaazhvu.mapping.views.helpers.data.QuestionDataHelper;
import com.puthuvaazhvu.mapping.views.helpers.flow.QuestionFlowHelperImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by muthuveerappans on 10/9/17.
 */

public class QuestionFragmentDataHelperTest {
    private SurveyDataModelTest surveyDataModelTest;
    private Question root;
    private Survey survey;
    private QuestionDataHelper questionDataHelper;

    @Before
    public void setup() {
        surveyDataModelTest = new SurveyDataModelTest();
        surveyDataModelTest.testSurveyModel();
        survey = surveyDataModelTest.survey;
        root = survey.getQuestionList().get(0);
        questionDataHelper = new QuestionDataHelper(root);
    }

    @Test
    public void test_getDataForGrid_method() {
        // get a random grid question to test
        Question gridQuestion = root.getChildren().get(1).getChildren().get(1).getChildren().get(6);
        assertThat(gridQuestion.getFlowPattern().getChildFlow().getUiToBeShown(), is(ChildFlow.UI.GRID));

        ArrayList<GridData> gridData = QuestionDataHelper.Adapters.getDataForGrid(gridQuestion);
        assertThat(gridData.size(), is(19));
        assertThat(gridData.get(1).getQuestion().getRawNumber(), is("2.1.7.4"));
    }

    @Test
    public void test_updateQuestion_method() {
        Question question_2_1_7 = root.getChildren().get(1).getChildren().get(1).getChildren().get(6);
        Question question_2_1_7_3 = question_2_1_7.getChildren().get(0);

        // create a mock option
        com.puthuvaazhvu.mapping.modals.Option mockSelectedOption = question_2_1_7_3.getOptionList().get(0);

        // mock what happens during the UI flow
        Data data = Data.adapter(question_2_1_7_3);
        Answer answer = new SingleAnswer(question_2_1_7_3.getId()
                , question_2_1_7_3.getTextString()
                , mockSelectedOption.getId()
                , mockSelectedOption.getTextString());
        com.puthuvaazhvu.mapping.views.fragments.option.modals.Data response
                = com.puthuvaazhvu.mapping.views.fragments.option.modals.Data.adapter(question_2_1_7_3);
        response.setAnswer(answer);
        data.setResponseData(response);

        // add the mock answer to the original reference question
        Question updatedQuestion = QuestionDataHelper.OtherHelpers.updateQuestion(data, question_2_1_7_3);

        // test if the option is added
        assertThat(updatedQuestion.getAnswer().size(), is(1));
        assertThat(updatedQuestion.getAnswer().get(0).getOptions().get(0).getId(), is(mockSelectedOption.getId()));
    }
}
