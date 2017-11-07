package com.puthuvaazhvu.mapping.presenters;

import com.puthuvaazhvu.mapping.helpers.ModalHelpers;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.AnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.message.Contract;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.message.Presenter;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.SingleQuestion;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static junit.framework.Assert.assertSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by muthuveerappans on 11/7/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class MessageQuestionPresenterTest {

    @Mock
    Contract.View view;

    private Presenter presenter;

    private Question root;

    @Mock
    private QuestionData questionData;

    @Captor
    private ArgumentCaptor<ArrayList<QuestionData>> captorAdapterDataFetched;

    @Before
    public void setup() {
        root = ModalHelpers.getMessageQuestion(this);

        assertThat(root.getRawNumber(), is("7.1"));

        presenter = new Presenter(root, view);
    }

    @Test
    public void test_getAdapterData_method() {
        presenter.getAdapterData();
        verify(view).onAdapterFetched(captorAdapterDataFetched.capture());
        assertThat(captorAdapterDataFetched.getValue().size(), is(13));
    }

    @Test
    public void test_populateAdapterData_method() {
        ArrayList<Question> adapterData = new ArrayList<>();
        ArrayList<Question> questions = Presenter.populateAdapterData(adapterData, root);

        assertThat(root.getAnswers().size(), is(1));
        assertSame(root.getCurrentAnswer().getChildren().get(0), questions.get(0));
        assertThat(questions.size(), is(13));
        assertThat(questions.get(8).getRawNumber(), is("7.1.8.1"));

        for (Question q : questions) {
            assertThat(q.getAnswers().size(), is(1));
        }
    }

    @Test
    public void test_updateAnswers_method() {
        // add dummy data
        QuestionData questionDataMock = mock(QuestionData.class);
        SingleQuestion singleQuestionMock = mock(SingleQuestion.class);

        when(questionDataMock.getSingleQuestion()).thenReturn(singleQuestionMock);
        when(questionDataMock.getSingleQuestion().getRawNumber()).thenReturn("7.1.8.1");

        ArrayList<Option> optionMock = new ArrayList<>();
        optionMock.add(new Option(
                        "1",
                        null,
                        null,
                        null,
                        null
                )
        );

        OptionData mockOptionData = mock(OptionData.class);
        AnswerData answerDataMock = mock(AnswerData.class);

        when(questionDataMock.getResponseData()).thenReturn(mockOptionData);
        when(questionDataMock.getResponseData().getAnswerData()).thenReturn(answerDataMock);
        when(questionDataMock.getResponseData().getAnswerData().getOption()).thenReturn(optionMock);

        ArrayList<QuestionData> questionDataList = new ArrayList<>();
        questionDataList.add(questionDataMock);

        presenter.getAdapterData();
        presenter.updateAnswers(questionDataList);

        assertThat(root.getAnswers().get(0).getChildren().get(7).getChildren().get(0).getRawNumber(), is("7.1.8.1"));
        assertThat(root.getAnswers().get(0).getChildren().get(7).getAnswers().get(0).getChildren().get(0).getCurrentAnswer()
                        .getOptions().get(0).getId()
                , is("1"));
    }
}
