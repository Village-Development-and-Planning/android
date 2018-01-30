package com.puthuvaazhvu.mapping;

import android.content.Context;

import com.puthuvaazhvu.mapping.helpers.TestUtils;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.SurveyUtils;

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
public class SurveyUtilsTest {
    @Mock
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSurveyModel() {
        Survey survey = TestUtils.getSurvey(this);

        assertThat(survey, is(notNullValue()));
    }

    @Test
    public void test_updateWithAnswers() {
        Single<Survey> surveySingle = SurveyUtils.getSurveyWithUpdatedAnswers(TestUtils.getAnswersJson(this));
        Survey survey = surveySingle.blockingGet();

        assertThat(survey.getRootQuestion().getAnswers().size(), is(1));
    }
}
