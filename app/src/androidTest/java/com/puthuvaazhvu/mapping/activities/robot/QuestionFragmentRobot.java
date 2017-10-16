package com.puthuvaazhvu.mapping.activities.robot;

import android.app.Activity;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.puthuvaazhvu.mapping.activities.matchers.LastChildMatcher.matchLastChild;
import static com.puthuvaazhvu.mapping.activities.matchers.RecyclerViewTextMatcher.withItemText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by muthuveerappans on 10/15/17.
 */

public class QuestionFragmentRobot {
    private final Activity activity;

    public QuestionFragmentRobot(Activity activity) {
        this.activity = activity;
    }

    public void waitToSync() {
        // Without waitForIdleSync(); our test would have nulls in fragment references.
        getInstrumentation().waitForIdleSync();
    }

    public void checkForToast(String message) {
        waitToSync();
        onView(withText(message))
                .inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    public void checkAndClickNextButton(ViewGroup container) {
        onView(matchLastChild(container, withId(R.id.next_button))).perform(click());
    }

    public void checkQuestionTextInFragment(String text) {
        onView(allOf(withId(R.id.question_text), withText(text)))
                .check(matches(isDisplayed()));
    }

    public void checkTextInRecyclerView(String text) {
        onView(withItemText(text))
                .check(matches(isDisplayed()));
    }

    public void selectOption(String text) {
        onView(withItemText(text))
                .check(matches(isDisplayed())).perform(click());
    }

    public void enterInFragmentEdt(String text) {
        onView(withId(R.id.input_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.input_edit_text)).perform(typeText(text), closeSoftKeyboard());
        onView(withText(text)).check(matches(isDisplayed()));
    }

    public void enterInFragmentEdt(String text, String contentDescription) {
        onView(allOf(withId(R.id.input_edit_text), withContentDescription(contentDescription))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.input_edit_text), withContentDescription(contentDescription))).perform(typeText(text), closeSoftKeyboard());
        onView(withText(text)).check(matches(isDisplayed()));
    }
}
