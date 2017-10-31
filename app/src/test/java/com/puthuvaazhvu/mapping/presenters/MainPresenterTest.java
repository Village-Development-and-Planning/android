package com.puthuvaazhvu.mapping.presenters;

import android.os.Handler;

import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.activities.main.Contract;
import com.puthuvaazhvu.mapping.views.activities.main.Presenter;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.helpers.FlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.IBackFlow;
import com.puthuvaazhvu.mapping.views.helpers.next_flow.IFlow;

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

    @Mock
    SaveToFile saveToFile;

    @Mock
    GetFromFile getFromFile;

    @Mock
    Handler handler;

    @Before
    public void setup() {
        presenter = new Presenter(viewCallback, dataRepository, handler, saveToFile, getFromFile);
        initQuestionMock();
    }

    public void initQuestionMock() {
        QuestionFlow mockQuestionFlow = new QuestionFlow(QuestionFlow.Validation.NONE, QuestionFlow.UI.SINGLE_CHOICE);
        FlowPattern mockFlowPattern = new FlowPattern(null, mockQuestionFlow, null, null, null, null);

        when(questionMock.getChildren()).thenReturn(new ArrayList<Question>());
        when(questionMock.getFlowPattern()).thenReturn(mockFlowPattern);
        when(questionMock.getRawNumber()).thenReturn("1");
        when(questionMock.getCurrentAnswer()).thenReturn(new Answer(null, null));
    }

    @Test
    public void test_moveToQuestionAt_method() {
        QuestionFlow mockQuestionFlow = new QuestionFlow(QuestionFlow.Validation.NONE, QuestionFlow.UI.SINGLE_CHOICE);
        FlowPattern mockFlowPattern = new FlowPattern(null, mockQuestionFlow, null, null, null, null);

        IFlow iFlow = mock(IFlow.class);
        when(iFlow.getCurrent()).thenReturn(questionMock);

        when(flowHelper.moveToIndex(anyInt())).thenReturn(iFlow);
        when(flowHelper.getCurrent()).thenReturn(questionMock);

        IFlow.FlowData f = new IFlow.FlowData();
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

        IFlow.FlowData f = new IFlow.FlowData();
        f.flowType = FlowType.SINGLE;
        f.question = questionMock;
        when(flowHelper.getNext()).thenReturn(f);

        presenter.getNext();

        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue(), notNullValue());

        //              -- GRID --

        f = new IFlow.FlowData();
        f.flowType = FlowType.GRID;
        f.question = questionMock;
        when(flowHelper.getNext()).thenReturn(f);

        presenter.getNext();

        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue(), notNullValue());
    }

    @Test
    public void test_getPrevious_method() {
        presenter.setSurveyQuestionFlow(flowHelper);

        // test for error
        IBackFlow.BackFlowData backFlowData = new IBackFlow.BackFlowData();
        backFlowData.question = null;
        when(flowHelper.getPrevious()).thenReturn(backFlowData);

        presenter.getPrevious();

        verify(viewCallback).onError(anyInt());

        ArgumentCaptor<QuestionData> captor = ArgumentCaptor.forClass(QuestionData.class);

        // test for success
        backFlowData = new IBackFlow.BackFlowData();
        backFlowData.question = questionMock;
        when(flowHelper.getPrevious()).thenReturn(backFlowData);

        presenter.getPrevious();

        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue(), notNullValue());
    }
}
