package com.puthuvaazhvu.mapping.presenters;

import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Flow.FlowPattern;
import com.puthuvaazhvu.mapping.modals.Flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.activities.Contract;
import com.puthuvaazhvu.mapping.views.activities.Presenter;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.helpers.FlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.IFlowHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by muthuveerappans on 10/14/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {
    @Mock
    DataRepository<Survey> dataRepository;

    @Mock
    Question questionMock;

    @Mock
    FlowHelper flowHelper;

    private Presenter presenter;

    @Mock
    private Contract.View viewCallback;

    @Before
    public void setup() {
        presenter = new Presenter(viewCallback, dataRepository);
        initQuestionMock();
    }

    public void initQuestionMock() {
        QuestionFlow mockQuestionFlow = new QuestionFlow(QuestionFlow.Validation.NONE, QuestionFlow.UI.SINGLE_CHOICE);
        FlowPattern mockFlowPattern = new FlowPattern(null, mockQuestionFlow, null, null, null, null);

        when(questionMock.getChildren()).thenReturn(new ArrayList<Question>());
        when(questionMock.getFlowPattern()).thenReturn(mockFlowPattern);
        when(questionMock.getRawNumber()).thenReturn("1");
        when(questionMock.getLatestAnswer()).thenReturn(new Answer(null, new ArrayList<Question>(), null));
    }

    @Test
    public void test_startSurvey_method() {
        IFlowHelper.FlowData flowData = new IFlowHelper.FlowData();
        flowData.flowType = FlowType.SINGLE;
        flowData.question = questionMock;

        when(flowHelper.getNext()).thenReturn(flowData);

        presenter.startSurvey(null, flowHelper);

        ArgumentCaptor<QuestionData> captor = ArgumentCaptor.forClass(QuestionData.class);
        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue(), notNullValue());
    }

    @Test
    public void test_getSurvey_method() {
        // get survey callback mock flow
        presenter.getSurvey();
        // Capture the argument of the callback function
        ArgumentCaptor<DataRepository.DataLoadedCallback> captor
                = ArgumentCaptor.forClass(DataRepository.DataLoadedCallback.class);
        verify(dataRepository).getData(anyString(), captor.capture());
        captor.getValue().onDataLoaded(null);

        // Check if the survey is passed to the view callback
        verify(viewCallback).onSurveyLoaded(null);
    }

    @Test
    public void test_moveToQuestionAt_method() {
        QuestionFlow mockQuestionFlow = new QuestionFlow(QuestionFlow.Validation.NONE, QuestionFlow.UI.SINGLE_CHOICE);
        FlowPattern mockFlowPattern = new FlowPattern(null, mockQuestionFlow, null, null, null, null);

        IFlowHelper iFlowHelper = mock(IFlowHelper.class);
        when(iFlowHelper.getCurrent()).thenReturn(questionMock);

        when(flowHelper.moveToIndex(anyInt())).thenReturn(iFlowHelper);
        when(flowHelper.getCurrent()).thenReturn(questionMock);

        IFlowHelper.FlowData f = new IFlowHelper.FlowData();
        f.question = questionMock;
        f.flowType = FlowType.SINGLE;
        when(flowHelper.getNext()).thenReturn(f);

        presenter.setSurveyQuestionFlow(flowHelper);
        presenter.moveToQuestionAt(1);

        ArgumentCaptor<QuestionData> captor = ArgumentCaptor.forClass(QuestionData.class);
        verify(viewCallback).shouldShowSingleQuestion(captor.capture());

        assertThat(captor.getValue().getSingleQuestion().getRawNumber(), is("1"));
    }

    @Test
    public void test_getNext_method() {
        ArgumentCaptor<QuestionData> captor = ArgumentCaptor.forClass(QuestionData.class);

        when(questionMock.getChildren()).thenReturn(new ArrayList<Question>());

        presenter.setSurveyQuestionFlow(flowHelper);

        //              -- SINGLE --

        IFlowHelper.FlowData f = new IFlowHelper.FlowData();
        f.flowType = FlowType.SINGLE;
        f.question = questionMock;
        when(flowHelper.getNext()).thenReturn(f);

        presenter.getNext();

        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue(), notNullValue());

        //              -- GRID --

        f = new IFlowHelper.FlowData();
        f.flowType = FlowType.GRID;
        f.question = questionMock;
        when(flowHelper.getNext()).thenReturn(f);

        presenter.getNext();

        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue(), notNullValue());
    }
}
