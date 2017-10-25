package com.puthuvaazhvu.mapping.activities;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.activities.robot.QuestionFragmentRobot;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.activities.main.MainActivity;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridQuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.v4.util.Preconditions.checkArgument;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
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

    private MainActivity activity;
    private ViewGroup container;

    private Survey survey;

    @Before
    public void init() throws IOException {
        activity = mActivityTestRule.getActivity();
        container = activity.findViewById(R.id.container);
        survey = ModalHelpers.getSurvey(activity);
    }

    @Test
    public void test_shouldShowSummary_method() {
        activity.shouldShowSummary(survey);
        getInstrumentation().waitForIdleSync();
        onView(withText(activity.getString(R.string.summary))).check(matches(isDisplayed()));
    }

    @Test
    public void test_grid_questions_UI() {
        //mock UI data
        // 2.1.7
        Question question = survey.getQuestionList().get(0)
                .getChildren().get(1).getChildren().get(1).getChildren().get(6);

        QuestionData parentQuestionData = QuestionData.adapter(question);

        ArrayList<GridQuestionData> gridQuestionData = GridQuestionData.adapter(question.getChildren());

        assertThat(question.getRawNumber(), is("2.1.7"));

        activity.shouldShowGrid(parentQuestionData, gridQuestionData);

        QuestionFragmentRobot questionFragmentRobot = new QuestionFragmentRobot(activity);

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkTextInRecyclerView("Tag a street light");
    }

    @Test
    public void test_conformationQuestion_UI() {
        // mock ui data
        Question question = survey.getQuestionList().get(0).getChildren().get(0).getChildren().get(0);

        assertThat(question.getRawNumber(), is("1.2"));

        QuestionData questionData = QuestionData.adapter(question);

        activity.shouldShowConformationQuestion(questionData);

        QuestionFragmentRobot questionFragmentRobot = new QuestionFragmentRobot(activity);

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkQuestionTextInFragment(question.getTextString());

        questionFragmentRobot.checkAndClickNextButton(container);

        assertThat(questionData.getResponseData().getAnswerData().getOption().get(0).getTextString(), is("YES"));
    }

    @Test
    public void test_singleQuestion_input_options_UI() {
        // mock ui data
        // 2.1.7.3.2
        Question question = survey.getQuestionList().get(0)
                .getChildren().get(1).getChildren().get(1).getChildren().get(6).getChildren().get(0).getChildren().get(1);

        assertThat(question.getRawNumber(), is("2.1.7.3.2"));

        QuestionData questionData = QuestionData.adapter(question);

        activity.shouldShowSingleQuestion(questionData);

        QuestionFragmentRobot questionFragmentRobot = new QuestionFragmentRobot(activity);

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkQuestionTextInFragment("2.1.7.3.2. " + question.getTextString());

        // should show error when edt is empty
        // check if error is displayed if no option is selected
        questionFragmentRobot.checkAndClickNextButton(container);
        questionFragmentRobot.checkForToast(activity.getString(R.string.options_not_entered_err));

        questionFragmentRobot.enterInFragmentEdt("123", question.getRawNumber());

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkAndClickNextButton(container);

        assertThat(questionData.getResponseData().getAnswerData().getOption().get(0).getText().getEnglish(), is("123"));
    }

    @Test
    public void test_singleQuestion_radioButton_options_UI() {
        // mock ui data
        // 2.1.3
        Question question = survey.getQuestionList().get(0)
                .getChildren().get(1).getChildren().get(1).getChildren().get(2);

        assertThat(question.getRawNumber(), is("2.1.3"));

        QuestionData questionData = QuestionData.adapter(question);

        activity.shouldShowSingleQuestion(questionData);

        QuestionFragmentRobot questionFragmentRobot = new QuestionFragmentRobot(activity);

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkQuestionTextInFragment("2.1.3. " + question.getTextString());

        // check if error is displayed if no option is selected
        questionFragmentRobot.checkAndClickNextButton(container);
        questionFragmentRobot.checkForToast(activity.getString(R.string.options_not_entered_err));

        questionFragmentRobot.selectOption(question.getOptionList().get(0).getTextString());
        questionFragmentRobot.selectOption(question.getOptionList().get(1).getTextString());

        assertThat(questionData.getOptionOptionData().getOptions().get(0).isSelected(), is(false));
        assertThat(questionData.getOptionOptionData().getOptions().get(1).isSelected(), is(true));
    }

    @Test
    public void test_singleQuestion_checkbox_options_UI() {
        // mock ui data
        // 2.1.7.4.4
        Question question = survey.getQuestionList().get(0)
                .getChildren().get(1).getChildren().get(1).getChildren().get(6).getChildren().get(1).getChildren().get(3);

        assertThat(question.getRawNumber(), is("2.1.7.4.4"));

        QuestionData questionData = QuestionData.adapter(question);

        activity.shouldShowSingleQuestion(questionData);

        QuestionFragmentRobot questionFragmentRobot = new QuestionFragmentRobot(activity);

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkQuestionTextInFragment("2.1.7.4.4. " + question.getTextString());

        // check if error is displayed if no option is selected
        questionFragmentRobot.checkAndClickNextButton(container);
        questionFragmentRobot.checkForToast(activity.getString(R.string.options_not_entered_err));

        questionFragmentRobot.selectOption(question.getOptionList().get(0).getTextString());
        questionFragmentRobot.selectOption(question.getOptionList().get(1).getTextString());

        assertThat(questionData.getOptionOptionData().getOptions().get(0).isSelected(), is(true));
        assertThat(questionData.getOptionOptionData().getOptions().get(1).isSelected(), is(true));

        // test un-checking
        questionFragmentRobot.selectOption(question.getOptionList().get(1).getTextString());
        assertThat(questionData.getOptionOptionData().getOptions().get(1).isSelected(), is(false));
    }
}
