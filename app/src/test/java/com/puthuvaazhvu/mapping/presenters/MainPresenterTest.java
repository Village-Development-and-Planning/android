package com.puthuvaazhvu.mapping.presenters;

import android.os.Handler;

import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.activities.main.Contract;
import com.puthuvaazhvu.mapping.views.activities.main.MainPresenter;
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

import static junit.framework.Assert.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by muthuveerappans on 10/14/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {
    @Mock
    SurveyDataRepository dataRepository;

    @Mock
    Question questionMock;

    @Mock
    FlowHelper flowHelper;

    private MainPresenter mainPresenter;

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
        mainPresenter = new MainPresenter(viewCallback, dataRepository, handler, saveToFile, getFromFile);
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
    public void test_getPathOfCurrentQuestion() {

        /*
            Construct a mock question tree
            R
            |_
            | 1
            | |_
            | | 1.1
            | |_
            |   1.2
            |_
              2
              |_
                2.1
         */

        Question root = mock(Question.class);

        Question c1 = mock(Question.class);
        Question c1_1 = mock(Question.class);
        Question c1_2 = mock(Question.class);

        Question c2 = mock(Question.class);
        Question c2_1 = mock(Question.class);

        // add raw number
        when(root.getRawNumber()).thenReturn("0");
        when(c1.getRawNumber()).thenReturn("1");
        when(c1_1.getRawNumber()).thenReturn("1.1");
        when(c1_2.getRawNumber()).thenReturn("1.2");
        when(c2.getRawNumber()).thenReturn("2");
        when(c2_1.getRawNumber()).thenReturn("2.1");

        // construct the tree
        ArrayList<Question> children = new ArrayList<>();

        children.add(c1);
        children.add(c2);
        when(root.getChildren()).thenReturn(children);

        children = new ArrayList<>();

        children.add(c1_1);
        children.add(c1_2);
        when(c1.getChildren()).thenReturn(children);

        children = new ArrayList<>();

        children.add(c2_1);
        when(c2.getChildren()).thenReturn(children);

        // add answer
        when(root.copy()).thenReturn(root);
        Answer answer = new Answer(null, root);
        ArrayList<Answer> answers = new ArrayList<>();
        answers.add(answer);
        when(root.getAnswers()).thenReturn(answers);
        when(root.getCurrentAnswer()).thenReturn(answer);

        when(c1.getParent()).thenReturn(root);
        when(c1.copy()).thenReturn(c1);
        answer = new Answer(null, c1);
        answers = new ArrayList<>();
        answers.add(answer);
        when(c1.getAnswers()).thenReturn(answers);
        when(c1.getCurrentAnswer()).thenReturn(answer);

        when(c2.getParent()).thenReturn(root);
        when(c2.copy()).thenReturn(c2);
        answer = new Answer(null, c2);
        answers = new ArrayList<>();
        answers.add(answer);
        when(c2.getAnswers()).thenReturn(answers);
        when(c2.getCurrentAnswer()).thenReturn(answer);

        when(c1_1.getParent()).thenReturn(c1);
        when(c1_1.copy()).thenReturn(c1_1);
        answer = new Answer(null, c1_1);
        answers = new ArrayList<>();
        answers.add(answer);
        when(c1_1.getAnswers()).thenReturn(answers);
        when(c1_1.getCurrentAnswer()).thenReturn(answer);

        when(c1_2.getParent()).thenReturn(c1);
        when(c1_2.copy()).thenReturn(c1_2);
        answer = new Answer(null, c1_2);
        answers = new ArrayList<>();
        answers.add(answer);
        when(c1_2.getAnswers()).thenReturn(answers);
        when(c1_2.getCurrentAnswer()).thenReturn(answer);

        when(c2_1.getParent()).thenReturn(c2);
        when(c2_1.copy()).thenReturn(c2_1);
        answer = new Answer(null, c2_1);
        answers = new ArrayList<>();
        answers.add(answer);
        when(c2_1.getAnswers()).thenReturn(answers);
        when(c2_1.getCurrentAnswer()).thenReturn(answer);

        assertThat(root.getAnswers().size(), is(1));
        assertThat(root.getChildren().size(), is(2));
        assertThat(c1.getChildren().size(), is(2));
        assertThat(c1.getAnswers().size(), is(1));
        assertThat(c1.getAnswers().get(0).getQuestionReference().getParent().getRawNumber(), is("0"));
        assertSame(c1_1.getAnswers().get(0).getQuestionReference().getParent(), c1);

        ArrayList<Integer> indexes = MainPresenter.getPathOfCurrentQuestion(c1_2);

        assertThat(indexes.size(), is(4));
        assertThat(indexes.get(3), is(0));

        indexes = MainPresenter.getPathOfCurrentQuestion(c2_1);

        assertThat(indexes.size(), is(4));
        assertThat(indexes.get(3), is(0));
        assertThat(indexes.get(2), is(1));
        assertThat(indexes.get(1), is(0));
        assertThat(indexes.get(0), is(0));
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

        mainPresenter.setSurveyQuestionFlow(flowHelper);
        mainPresenter.moveToQuestionAt(1);

        ArgumentCaptor<QuestionData> captor = ArgumentCaptor.forClass(QuestionData.class);
        verify(viewCallback).shouldShowSingleQuestion(captor.capture());

        assertThat(captor.getValue().getSingleQuestion().getRawNumber(), is("1"));
    }

    @Test
    public void test_getNext_method() {
        ArgumentCaptor<QuestionData> captor = ArgumentCaptor.forClass(QuestionData.class);

        when(questionMock.getChildren()).thenReturn(new ArrayList<Question>());

        mainPresenter.setSurveyQuestionFlow(flowHelper);

        //              -- SINGLE --

        IFlow.FlowData f = new IFlow.FlowData();
        f.flowType = FlowType.SINGLE;
        f.question = questionMock;
        when(flowHelper.getNext()).thenReturn(f);

        mainPresenter.getNext();

        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue(), notNullValue());

        //              -- GRID --

        f = new IFlow.FlowData();
        f.flowType = FlowType.GRID;
        f.question = questionMock;
        when(flowHelper.getNext()).thenReturn(f);

        mainPresenter.getNext();

        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue(), notNullValue());
    }

    @Test
    public void test_getPrevious_method() {
        mainPresenter.setSurveyQuestionFlow(flowHelper);

        // test for error
        IBackFlow.BackFlowData backFlowData = new IBackFlow.BackFlowData();
        backFlowData.question = null;
        when(flowHelper.getPrevious()).thenReturn(backFlowData);

        mainPresenter.getPrevious();

        verify(viewCallback).onError(anyInt());

        ArgumentCaptor<QuestionData> captor = ArgumentCaptor.forClass(QuestionData.class);

        // test for success
        backFlowData = new IBackFlow.BackFlowData();
        backFlowData.question = questionMock;
        when(flowHelper.getPrevious()).thenReturn(backFlowData);

        mainPresenter.getPrevious();

        verify(viewCallback).shouldShowSingleQuestion(captor.capture());
        assertThat(captor.getValue(), notNullValue());
    }
}
