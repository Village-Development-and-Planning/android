package com.puthuvaazhvu.mapping.presenters;

import com.puthuvaazhvu.mapping.modals.SurveyDataModelTest;
import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.activities.Contract;
import com.puthuvaazhvu.mapping.views.activities.Presenter;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;
import com.puthuvaazhvu.mapping.views.helpers.data.QuestionDataHelper;
import com.puthuvaazhvu.mapping.views.helpers.flow.QuestionFlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.flow.QuestionFlowHelperImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by muthuveerappans on 10/6/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock
    DataRepository<Survey> dataRepository;

    private Presenter presenter;
    private SurveyDataModelTest surveyDataModelTest;
    private Survey survey;

    @Mock
    private Contract.View viewCallback;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        presenter = new Presenter(viewCallback, dataRepository);
        surveyDataModelTest = new SurveyDataModelTest();
        surveyDataModelTest.testSurveyModel();
        survey = surveyDataModelTest.survey;
    }

    @Test
    public void test_getSurvey_method() {
        // get survey callback mock flow
        presenter.getSurvey();
        // Capture the argument of the callback function
        ArgumentCaptor<DataRepository.DataLoadedCallback> captor
                = ArgumentCaptor.forClass(DataRepository.DataLoadedCallback.class);
        verify(dataRepository).getData(anyString(), captor.capture());
        captor.getValue().onDataLoaded(survey);

        // Check if the survey is passed to the view callback
        verify(viewCallback).onSurveyLoaded(survey);
    }

    @Test
    public void test_setCurrentQuestion_method() {
        Question root = survey.getQuestionList().get(0);
        presenter.setData(survey, new QuestionFlowHelperImpl(root), new QuestionDataHelper(root));

        // get a mock question form survey
        Data mockData = Data.adapter(survey.getQuestionList().get(0).getChildren().get(0));
        presenter.setCurrentQuestion(mockData);

        ArgumentCaptor<Data> captor = ArgumentCaptor.forClass(Data.class);

        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue().getQuestion().getId(), is(mockData.getQuestion().getId()));

        // try passing a grid question
        Question question_2_1_7 = survey.getQuestionList().get(0)
                .getChildren().get(1).getChildren().get(1).getChildren().get(6);
        mockData = Data.adapter(question_2_1_7);
        presenter.setCurrentQuestion(mockData);

        ArgumentCaptor<ArrayList> gridDataCaptor = ArgumentCaptor.forClass(ArrayList.class);

        verify(viewCallback).shouldShowGrid(anyString(), gridDataCaptor.capture());
        assertThat(gridDataCaptor.getValue().size(), is(question_2_1_7.getChildren().size()));

        // try passing a conformation question
        // 2.1
        Question question_2_1 = survey.getQuestionList().get(0)
                .getChildren().get(1).getChildren().get(1);
        mockData = Data.adapter(question_2_1);
        presenter.setCurrentQuestion(mockData);

        ArgumentCaptor<Data> singleQuestionCaptor = ArgumentCaptor.forClass(Data.class);

        verify(viewCallback).shouldShowConformationQuestion(singleQuestionCaptor.capture());
        assertThat(singleQuestionCaptor.getValue().getQuestion().getRawNumber()
                , is("2.1"));
    }

    @Test
    public void test_getNext_method() {
        // get a mock question form survey
        // try passing a grid question
        Question question_2_1_7 = survey.getQuestionList().get(0)
                .getChildren().get(1).getChildren().get(1).getChildren().get(6);

        QuestionFlowHelper questionFlowHelper = mock(QuestionFlowHelper.class);
        QuestionDataHelper questionDataHelper = mock(QuestionDataHelper.class);

        when(questionFlowHelper.getNext()).thenReturn(question_2_1_7);

        presenter.setData(survey, questionFlowHelper, questionDataHelper);
        presenter.getNext();

        ArgumentCaptor<ArrayList> gridDataCaptor = ArgumentCaptor.forClass(ArrayList.class);

        verify(viewCallback).shouldShowGrid(anyString(), gridDataCaptor.capture());
        assertThat(gridDataCaptor.getValue().size(), is(question_2_1_7.getChildren().size()));
    }

    @Test
    public void test_startSurvey_method() {
        presenter.startSurvey(survey);

        ArgumentCaptor<Data> captor = ArgumentCaptor.forClass(Data.class);
        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue().getQuestion().getRawNumber(), is("1"));
    }

}
