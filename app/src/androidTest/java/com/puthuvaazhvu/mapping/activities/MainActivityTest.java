package com.puthuvaazhvu.mapping.activities;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.MainActivity;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.EditTextOptionFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.OptionsListFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Option;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.InfoFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.QuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.SingleQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.v4.util.Preconditions.checkArgument;
import static com.puthuvaazhvu.mapping.activities.matchers.LastChildMatcher.matchLastChild;
import static com.puthuvaazhvu.mapping.activities.matchers.RecyclerViewTextMatcher.withItemText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNot.not;

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
    private ViewGroup container;

    public static Matcher<View> matchLast(final Matcher<View> matcher, final ViewGroup root) {
        return new TypeSafeMatcher<View>() {
            int childCount = 0;
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                for (int i = 0; i < root.getChildCount(); i++) {
                    childCount = i;
                }
                return matcher.matches(view) && currentIndex++ == childCount;
            }
        };
    }

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
        container = activity.findViewById(R.id.container);

        root = survey.getQuestionList().get(0);
    }

//    @Test
//    public void testGridFragmentFlow() {
//        Question visibleQuestion;
//        Question parent = root.getChildren().get(1).getChildren().get(1).getChildren().get(6);
//
//        // 2.1.7
//        waitToSync();
//        visibleQuestion = parent;
//        assertThat(visibleQuestion.getRawNumber(), is("2.1.7"));
//        setCurrentQuestion(visibleQuestion);
//        selectOption(visibleQuestion, "YES");
//        checkQuestionTextInFragment(visibleQuestion);
//        checkAndMoveToNextFragment(container);
//
//        // 2.1.7.3
//        waitToSync();
//        visibleQuestion = parent.getChildren().get(0);
//        assertThat(visibleQuestion.getRawNumber(), is("2.1.7.3"));
//        setCurrentQuestion(visibleQuestion);
//        selectOption(visibleQuestion, "NO");
//        checkQuestionTextInFragment(visibleQuestion);
//        checkAndMoveToNextFragment(container);
//
//        // 2.1.7
//        waitToSync();
//        visibleQuestion = parent;
//        checkQuestionTextInFragment(visibleQuestion);
//    }

    @Test
    public void testConformationFlow() {
        activity.onSurveyLoaded(survey);

        Question visibleQuestion;
        Question parent = root.getChildren().get(1).getChildren().get(1);

        // 2.1
        waitToSync();
        visibleQuestion = parent;
        assertThat(visibleQuestion.getRawNumber(), is("2.1"));
        setCurrentQuestion(visibleQuestion);
        checkQuestionTextInFragment(visibleQuestion);
        checkAndMoveToNextFragment(container);

        // 2.1.1
        waitToSync();
        visibleQuestion = parent.getChildren().get(0);
        assertThat(visibleQuestion.getRawNumber(), is("2.1.1"));
        selectRandomOptionInList(visibleQuestion);
        checkQuestionTextInFragment(visibleQuestion);
    }

    @Test
    public void testFragmentCascadeFlow() {
        Question visibleQuestion;
        Question parent = root.getChildren().get(0);

        // load the survey to the activity. At this point the first question should be shown on the screen.
        activity.onSurveyLoaded(survey);

        // 1
        waitToSync();
        visibleQuestion = parent;
        assertThat(visibleQuestion.getRawNumber(), is("1"));
        // enter something in the edt
        enterSomethingInFragmentEdt();
        checkQuestionTextInFragment(visibleQuestion);
        checkAndMoveToNextFragment(container);

        // 1.2
        waitToSync();
        visibleQuestion = parent.getChildren().get(0);
        assertThat(visibleQuestion.getRawNumber(), is("1.2"));
        checkQuestionTextInFragment(visibleQuestion);
        checkAndMoveToNextFragment(container);

        // 1.3
        waitToSync();
        visibleQuestion = parent.getChildren().get(1);
        checkQuestionTextInFragment(visibleQuestion);
        checkAndMoveToNextFragment(container);

        // 1.4
        waitToSync();
        visibleQuestion = parent.getChildren().get(2);
        checkQuestionTextInFragment(visibleQuestion);
        checkAndMoveToNextFragment(container);

        // 1.5
        waitToSync();
        visibleQuestion = parent.getChildren().get(3);
        checkQuestionTextInFragment(visibleQuestion);
        checkAndMoveToNextFragment(container);

        // 2 - select a random option
        waitToSync();
        visibleQuestion = root.getChildren().get(1);
        selectRandomOptionInList(visibleQuestion);
    }

    @Test
    public void testFragmentOptionsNotSelectedError() {
        // load the survey to the activity. At this point the first question should be shown on the screen.
        activity.onSurveyLoaded(survey);

        Question visibleQuestion;

        // 2 - No option selected
        waitToSync();
        visibleQuestion = root.getChildren().get(1).getChildren().get(1).getChildren().get(2);
        assertThat(visibleQuestion.getRawNumber(), is("2.1.3"));
        setCurrentQuestion(visibleQuestion);
        checkQuestionTextInFragment(visibleQuestion);
        checkAndMoveToNextFragment(container);

        checkForToast(activity.getString(R.string.options_not_entered_err));
    }

    @Test
    public void testFragmentSkip() {
        // load the survey to the activity. At this point the first question should be shown on the screen.
        activity.onSurveyLoaded(survey);

        Question visibleQuestion;

        // 2.1.5 - option NO
        waitToSync();
        visibleQuestion = root.getChildren().get(1).getChildren().get(1).getChildren().get(4);
        setCurrentQuestion(visibleQuestion);
        selectOption(visibleQuestion, "NO");
        checkQuestionTextInFragment(visibleQuestion);
        checkAndMoveToNextFragment(container);

        // 2.1.6
        waitToSync();
        visibleQuestion = root.getChildren().get(1).getChildren().get(1).getChildren().get(5);
        assertThat(visibleQuestion.getRawNumber(), is("2.1.6"));
        setCurrentQuestion(visibleQuestion);
        selectRandomOptionInList(visibleQuestion);
        checkQuestionTextInFragment(visibleQuestion);
        checkAndMoveToNextFragment(container);
    }

    private void setCurrentQuestion(Question visibleQuestion) {
        activity.setCurrentQuestion(Data.adapter(visibleQuestion));
        waitToSync();
    }

    private void selectOption(Question question, String optionText) {
        String text = "";
        String contentDescription = "";
        com.puthuvaazhvu.mapping.modals.Option option;
        int i = 0;

        do {
            option = question.getOptionList().get(i++);
            text = option.getText().getEnglish();
        } while (!text.toUpperCase().equals(optionText));

        contentDescription = option.getId();
        selectOption(text, contentDescription);
    }

    private void selectRandomOptionInList(Question question) {
        // get a random option
        com.puthuvaazhvu.mapping.modals.Option randomOption = question.getOptionList().get(0);
        String text = randomOption.getTextString();
        String contentDescription = randomOption.getId();
        selectOption(text, contentDescription);
    }

    private void selectOption(String text, String contentDescription) {
        onView(withItemText(text, contentDescription))
                .check(matches(isDisplayed())).perform(click());
    }

    private void checkQuestionTextInFragment(Question visibleQuestion) {
        onView(allOf(withId(R.id.question_text), withText(visibleQuestion.getTextString())))
                .check(matches(isDisplayed()));
    }

    private void checkAndMoveToNextFragment(ViewGroup container) {
        onView(matchLastChild(container, withId(R.id.next_button))).perform(click());
    }

    private void checkForToast(String message) {
        waitToSync();
        onView(withText(message))
                .inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    private void enterSomethingInFragmentEdt() {
        onView(withId(R.id.input_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.input_edit_text)).perform(typeText("TEST"), closeSoftKeyboard());
        onView(withText("TEST")).check(matches(isDisplayed()));
    }

    private static InputStream getDataFormFile(String fileName) throws IOException {
        Context testContext = InstrumentationRegistry.getContext();
        InputStream testInput = testContext.getResources().getAssets().open(fileName);
        return testInput;
    }

    private void waitToSync() {
        // Without waitForIdleSync(); our test would have nulls in fragment references.
        getInstrumentation().waitForIdleSync();
    }
}
