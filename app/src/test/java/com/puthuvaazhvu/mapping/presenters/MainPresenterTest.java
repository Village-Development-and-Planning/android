package com.puthuvaazhvu.mapping.presenters;

import android.os.Handler;

import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.activities.main.Contract;
import com.puthuvaazhvu.mapping.views.activities.main.MainPresenter;
import com.puthuvaazhvu.mapping.views.helpers.FlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
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
        when(questionMock.getLatestAnswer()).thenReturn(new Answer(null, null));
    }
}
