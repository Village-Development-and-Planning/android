package com.puthuvaazhvu.mapping.activities;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.MainActivity;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.Info;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.SingleQuestion;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by muthuveerappans on 10/10/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private Survey survey;
    private Question root;
    private MainActivity activity;

    @Before
    public void init() throws IOException {
        String fileName = "survey_6.json";
        String surveyDataString = Utils.readFromInputStream(getDataFormFile(fileName));

        assertThat(surveyDataString, notNullValue());

        assertThat(surveyDataString, containsString("_id"));

        JsonParser jsonParser = new JsonParser();
        JsonObject surveyJson = jsonParser.parse(surveyDataString).getAsJsonObject();

        assertThat(surveyJson, notNullValue());

        survey = new Survey(surveyJson);

        assertThat(survey, is(notNullValue()));

        activity = mActivityTestRule.getActivity();
        activity.onSurveyLoaded(survey);

        root = survey.getQuestionList().get(0);
    }

    @Test
    public void testActivityStart() {
        // 1
        Question visibleQuestion = root.getChildren().get(0);
        checkFragment(visibleQuestion, true);

        // 1.2
        visibleQuestion = root.getChildren().get(0).getChildren().get(0);
        checkFragment(visibleQuestion, true);

        // 1.3
        visibleQuestion = root.getChildren().get(0).getChildren().get(1);
        checkFragment(visibleQuestion, true);

        // 1.4
        visibleQuestion = root.getChildren().get(0).getChildren().get(2);
        checkFragment(visibleQuestion, true);

        // 1.5
        visibleQuestion = root.getChildren().get(0).getChildren().get(3);
        checkFragment(visibleQuestion, true);

        // 2
        visibleQuestion = root.getChildren().get(1);
        checkFragment(visibleQuestion, false);
    }

    private void checkFragment(Question question, boolean clickNext) {
        com.puthuvaazhvu.mapping.views.fragments.question.fragment.Question fragment
                = (com.puthuvaazhvu.mapping.views.fragments.question.fragment.Question) getFragment(question);

        assertThat(fragment, notNullValue());

        if (fragment instanceof SingleQuestion) {
            TextView textView = fragment.getView().findViewById(R.id.question_text);
            assertThat(textView.getText().toString(), is(question.getTextString()));
        }

        if (clickNext) {
            nextClickInFragment(fragment);
        }
    }

    private Fragment getFragment(Question question) {
        String tag = question.getId();
        waitForFragmentToSync();
        return findFragment(tag);
    }

    private Fragment findFragment(String tag) {
        return activity.getSupportFragmentManager().findFragmentByTag(tag);
    }

    private void nextClickInFragment(com.puthuvaazhvu.mapping.views.fragments.question.fragment.Question fragment) {
        if (fragment instanceof SingleQuestion || fragment instanceof Info) {
            Button nextButton = fragment.getView().findViewById(R.id.next_button);
            nextButton.performClick();
        }
    }

    private static InputStream getDataFormFile(String fileName) throws IOException {
        Context testContext = InstrumentationRegistry.getContext();
        InputStream testInput = testContext.getResources().getAssets().open(fileName);
        return testInput;
    }

    private void waitForFragmentToSync() {
        // Without waitForIdleSync(); our test would have nulls in fragment references.
        getInstrumentation().waitForIdleSync();
    }
}
