package com.puthuvaazhvu.mapping.modals;

import android.content.Context;

import com.puthuvaazhvu.mapping.helpers.ModalHelpers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Single;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by muthuveerappans on 10/6/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class SurveyDataModelTest {
    @Mock
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSurveyModel() {
        Survey survey = ModalHelpers.getSurvey(this);

        assertThat(survey, is(notNullValue()));
    }

    @Test
    public void test_updateWithAnswers() {
        Single<Survey> surveySingle = Survey.getSurveyInstanceWithUpdatedAnswers(ModalHelpers.getAnswersJson(this));
        Survey survey = surveySingle.blockingGet();

        assertThat(survey.getRootQuestion().getAnswers().size(), is(1));
    }
}
