package com.puthuvaazhvu.mapping.modals;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

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

    public Survey survey;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSurveyModel() {
        survey = ModalHelpers.getSurvey(this);

        assertThat(survey, is(notNullValue()));
    }
}
