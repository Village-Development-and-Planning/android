package com.puthuvaazhvu.mapping.activities;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.activities.robot.QuestionFragmentRobot;
import com.puthuvaazhvu.mapping.views.activities.MainActivity;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.SingleOptionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridQuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.SingleQuestion;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;

import static android.support.test.espresso.action.ViewActions.click;
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

    @Before
    public void init() throws IOException {
        activity = mActivityTestRule.getActivity();
        container = activity.findViewById(R.id.container);
    }

    @Test
    public void test_info_question_UI() {

    }

    @Test
    public void test_grid_questions_UI() {
        // generate mock UI data
        QuestionData parentQuestionData = new QuestionData(
                new SingleQuestion("1", "TEST", "1", null)
                , null
                , null);

        ArrayList<GridQuestionData> gridQuestionData = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            // mock ui data
            String questionID = "" + i;
            String text = "TEST QUESTION " + i;
            String rawNumber = "" + i;
            String questionPosition = "" + i;

            SingleQuestion singleQuestion = new SingleQuestion(questionID, text, rawNumber, questionPosition);

            OptionData optionData = new OptionData(questionID
                    , text
                    , null
                    , OptionData.Type.NONE
                    , null
                    , null);

            gridQuestionData.add(new GridQuestionData(singleQuestion, optionData, i));
        }

        activity.shouldShowGrid(parentQuestionData, gridQuestionData);

        QuestionFragmentRobot questionFragmentRobot = new QuestionFragmentRobot(activity);

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkTextInRecyclerView("TEST QUESTION 1");
    }

    @Test
    public void test_conformationQuestion_UI() {
        // mock ui data
        String questionID = "1";
        String text = "TEST QUESTION";
        String rawNumber = "1.0";
        String questionPosition = "0";

        SingleQuestion singleQuestion = new SingleQuestion(questionID, text, rawNumber, questionPosition);

        OptionData optionData = new OptionData(questionID
                , text
                , null
                , OptionData.Type.NONE
                , null
                , null);

        QuestionData questionData = new QuestionData(singleQuestion, optionData, null);

        assertThat(questionData.getSingleQuestion().getId(), is("1"));
        assertThat(questionData.getOptionOptionData().getOptions(), nullValue());

        activity.shouldShowConformationQuestion(questionData);

        QuestionFragmentRobot questionFragmentRobot = new QuestionFragmentRobot(activity);

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkQuestionTextInFragment(text);

        questionFragmentRobot.checkAndClickNextButton(container);

        assertThat(questionData.getResponseData().getAnswerData().getOption().get(0).getTextString(), is("CONFORMATION_DUMMY"));
    }

    @Test
    public void test_singleQuestion_input_options_UI() {
        // mock ui data
        String questionID = "1";
        String text = "TEST QUESTION";
        String rawNumber = "1.0";
        String questionPosition = "0";

        SingleQuestion singleQuestion = new SingleQuestion(questionID, text, rawNumber, questionPosition);

        OptionData optionData = new OptionData(questionID
                , text
                , null
                , OptionData.Type.EDIT_TEXT
                , null
                , null);

        QuestionData questionData = new QuestionData(singleQuestion, optionData, null);

        assertThat(questionData.getSingleQuestion().getId(), is("1"));
        assertThat(questionData.getOptionOptionData().getOptions(), nullValue());

        activity.shouldShowSingleQuestion(questionData);

        QuestionFragmentRobot questionFragmentRobot = new QuestionFragmentRobot(activity);

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkQuestionTextInFragment(text);

        // should show error when edt is empty
        // check if error is displayed if no option is selected
        questionFragmentRobot.checkAndClickNextButton(container);
        questionFragmentRobot.checkForToast(activity.getString(R.string.options_not_entered_err));

        questionFragmentRobot.enterInFragmentEdt("INPUT TEST");

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkAndClickNextButton(container);

        assertThat(optionData.getAnswerData().getOption().get(0).getText().getEnglish(), is("INPUT TEST"));
    }

    @Test
    public void test_singleQuestion_radioButton_options_UI() {
        // mock ui data
        String questionID = "1";
        String text = "TEST QUESTION";
        String rawNumber = "1.0";
        String questionPosition = "0";

        SingleQuestion singleQuestion = new SingleQuestion(questionID, text, rawNumber, questionPosition);

        ArrayList<SingleOptionData> singleOptionData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            singleOptionData.add(new SingleOptionData("" + i, "TEST OPTION " + i, "" + i, false));
        }

        OptionData optionData = new OptionData(questionID
                , text
                , null
                , OptionData.Type.RADIO_BUTTON_LIST
                , null
                , singleOptionData);

        QuestionData questionData = new QuestionData(singleQuestion, optionData, null);

        assertThat(questionData.getSingleQuestion().getId(), is("1"));
        assertThat(questionData.getOptionOptionData().getOptions().size(), is(5));

        activity.shouldShowSingleQuestion(questionData);

        QuestionFragmentRobot questionFragmentRobot = new QuestionFragmentRobot(activity);

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkQuestionTextInFragment(text);

        // check if error is displayed if no option is selected
        questionFragmentRobot.checkAndClickNextButton(container);
        questionFragmentRobot.checkForToast(activity.getString(R.string.options_not_entered_err));

        questionFragmentRobot.selectOption("TEST OPTION 0");
        questionFragmentRobot.selectOption("TEST OPTION 1");

        assertThat(optionData.getOptions().get(0).isSelected(), is(false));
        assertThat(optionData.getOptions().get(1).isSelected(), is(true));
    }

    @Test
    public void test_singleQuestion_checkbox_options_UI() {
        // mock ui data
        String questionID = "1";
        String text = "TEST QUESTION";
        String rawNumber = "1.0";
        String questionPosition = "0";

        SingleQuestion singleQuestion = new SingleQuestion(questionID, text, rawNumber, questionPosition);

        ArrayList<SingleOptionData> singleOptionData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            singleOptionData.add(new SingleOptionData("" + i, "TEST OPTION " + i, "" + i, false));
        }

        OptionData optionData = new OptionData(questionID
                , text
                , null
                , OptionData.Type.CHECKBOX_LIST
                , null
                , singleOptionData);

        QuestionData questionData = new QuestionData(singleQuestion, optionData, null);

        assertThat(questionData.getSingleQuestion().getId(), is("1"));
        assertThat(questionData.getOptionOptionData().getOptions().size(), is(5));

        activity.shouldShowSingleQuestion(questionData);

        QuestionFragmentRobot questionFragmentRobot = new QuestionFragmentRobot(activity);

        questionFragmentRobot.waitToSync();

        questionFragmentRobot.checkQuestionTextInFragment(text);

        // check if error is displayed if no option is selected
        questionFragmentRobot.checkAndClickNextButton(container);
        questionFragmentRobot.checkForToast(activity.getString(R.string.options_not_entered_err));

        questionFragmentRobot.selectOption("TEST OPTION 0");
        questionFragmentRobot.selectOption("TEST OPTION 1");

        assertThat(optionData.getOptions().get(0).isSelected(), is(true));
        assertThat(optionData.getOptions().get(1).isSelected(), is(true));

        // test un-checking
        questionFragmentRobot.selectOption("TEST OPTION 1");
        assertThat(optionData.getOptions().get(1).isSelected(), is(false));
    }
}
