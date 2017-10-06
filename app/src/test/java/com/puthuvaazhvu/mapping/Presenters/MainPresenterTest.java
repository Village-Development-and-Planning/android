package com.puthuvaazhvu.mapping.Presenters;

import com.puthuvaazhvu.mapping.Models.SurveyDataModelTest;
import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.activities.Contract;
import com.puthuvaazhvu.mapping.views.activities.Presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by muthuveerappans on 10/6/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest extends SurveyDataModelTest {

    @Mock
    Contract.View view;

    @Mock
    DataRepository<Survey> dataRepository;

    @Captor
    private ArgumentCaptor<DataRepository.DataLoadedCallback<Survey>> dataLoadedCallbackArgumentCaptor;

    private Presenter presenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        presenter = new Presenter(view, dataRepository);
    }

    @Test
    public void testQuestionsFlow() {
        super.testSurveyModel();

        presenter.getSurvey();

        // Callback is captured and invoked with stubbed survey data
        verify(dataRepository).getData(anyString(), dataLoadedCallbackArgumentCaptor.capture());
        dataLoadedCallbackArgumentCaptor.getValue().onDataLoaded(survey);

        verify(view).onSurveyLoaded(survey);

        for (int i = 0; i < 6; i++) {
            moveToNextQuestion();
        }
        assertThat(presenter.getCurrentQuestion().getRawNumber(), is("2"));

        presenter.setCurrentQuestion(presenter.getCurrentQuestion().getChildren().get(1));
        assertThat(presenter.getCurrentQuestion().getRawNumber(), is("2.2"));
        for (int i = 0; i < 6; i++) {
            moveToNextQuestion();
        }
        // After 4 removes the UI will point to Question 2
        ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(view, atLeastOnce()).remove(captor.capture());
        ArrayList<Question> removed = captor.getValue();
        assertThat(removed.size(), is(4));
        assertThat(presenter.getCurrentQuestion().getRawNumber(), is("2"));

        presenter.setCurrentQuestion(presenter.getCurrentQuestion().getChildren().get(13));
        assertThat(presenter.getCurrentQuestion().getRawNumber(), is("2.14"));
        for (int i = 0; i < 9; i++) {
            moveToNextQuestion();
        }
        // After 8 removes the UI will point to Question 2
        captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(view, atLeastOnce()).remove(captor.capture());
        removed = captor.getValue();
        assertThat(removed.size(), is(8));
        assertThat(presenter.getCurrentQuestion().getRawNumber(), is("2"));

        presenter.setCurrentQuestion(presenter.getCurrentQuestion().getChildren().get(14));
        assertThat(presenter.getCurrentQuestion().getRawNumber(), is("2.15"));
        for (int i = 0; i < 10; i++) {
            moveToNextQuestion();
        }
        // After 5 removes the UI will point to Question 2
        captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(view, atLeastOnce()).remove(captor.capture());
        removed = captor.getValue();
        assertThat(removed.size(), is(5));
        assertThat(presenter.getCurrentQuestion().getRawNumber(), is("2"));

    }

    private void moveToNextQuestion() {
        presenter.getNext();
        presenter.getCurrentQuestion().addAnswer(new Answer(null, null)); // add mock answer
    }

}
