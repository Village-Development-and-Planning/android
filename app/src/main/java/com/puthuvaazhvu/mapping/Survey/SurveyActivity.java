package com.puthuvaazhvu.mapping.Survey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Modals.Survey;
import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.Loop.QuestionTreeRootLoopFragment;
import com.puthuvaazhvu.mapping.Question.Loop.QuestionTreeRootLoopFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragment;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Question.SingleQuestion.QuestionFragment;
import com.puthuvaazhvu.mapping.Question.SingleQuestion.QuestionFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.Test.SurveyTestFragment;
import com.puthuvaazhvu.mapping.utils.DataHelper;

import java.util.ArrayList;
import java.util.HashMap;

import static com.puthuvaazhvu.mapping.Constants.DEBUG;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class SurveyActivity extends AppCompatActivity implements SurveyActivityCommunicationInterface {
    String surveyJSON;
    SurveyActivityPresenter surveyActivityPresenter;
    QuestionTreeFragment questionTreeFragment;
    QuestionTreeRootLoopFragment questionTreeRootLoopFragment;
    String basePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.survey_activity);

        basePath = getFilesDir().getAbsolutePath();

        if (DEBUG) {
            surveyJSON = DataHelper.readFromAssetsFile(this, "test_data/survey_test_options2.json");
        } else {
            surveyJSON = getIntent().getExtras().getString(Constants.IntentKeys.SurveyActivity_survey_data_string);
        }

        surveyActivityPresenter = new SurveyActivityPresenter(this);
        surveyActivityPresenter.parseSurveyJson(surveyJSON);
    }

    @Override
    public void parsedSurveyData(Survey survey) {
//        if (DEBUG) {
//            SurveyTestFragment surveyTestFragment = SurveyTestFragment.getInstance(survey);
//            FragmentTransaction f = getSupportFragmentManager().beginTransaction();
//            f.replace(R.id.question_container, surveyTestFragment);
//            f.commitAllowingStateLoss();
//        }
        surveyActivityPresenter.getNextQuestionAndDirectToFragments();
    }

    @Override
    public void loadQuestionFragment(QuestionModal questionModal) {
        loadQuestionTreeFragment(questionModal);
    }

    @Override
    public void loadLoopQuestionFragment(QuestionModal questionModal) {
        loadQuestionLoopFragment(questionModal);
    }

    @Override
    public void onSurveyDone() {
        if (DEBUG)
            Toast.makeText(SurveyActivity.this, "Survey is done.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAllQuestionsSaved() {
        if (DEBUG)
            Toast.makeText(SurveyActivity.this, "Survey answers have been saved successfully."
                    , Toast.LENGTH_SHORT).show();
    }

    private void loadQuestionTreeFragment(QuestionModal questionModal) {
        if (questionModal == null) {
            throw new RuntimeException("The question data is null");
        }

        questionTreeFragment = null;
        questionTreeFragment = QuestionTreeFragment.getInstance(questionModal);
        questionTreeFragment.setCommunicationInterface(questionTreeFragmentCommunicationInterface);

        replaceFragment(questionTreeFragment);
    }


    private void loadQuestionLoopFragment(QuestionModal questionModal) {
        if (questionModal == null) {
            throw new RuntimeException("The question data is null");
        }

        questionTreeRootLoopFragment = null;
        questionTreeRootLoopFragment = QuestionTreeRootLoopFragment.getInstance(questionModal);
        questionTreeRootLoopFragment.setCommunicationInterface(questionTreeRootLoopFragmentCommunicationInterface);

        replaceFragment(questionTreeRootLoopFragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.question_container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private QuestionTreeFragmentCommunicationInterface questionTreeFragmentCommunicationInterface = new QuestionTreeFragmentCommunicationInterface() {
        @Override
        public void onFinished(QuestionModal modifiedQuestionModal) {
            // NOT IMPLEMENTED
        }
    };

    private QuestionTreeRootLoopFragmentCommunicationInterface questionTreeRootLoopFragmentCommunicationInterface = new QuestionTreeRootLoopFragmentCommunicationInterface() {
        @Override
        public void onLoopFinished(HashMap<String, HashMap<String, QuestionModal>> result) {
            surveyActivityPresenter.logAnsweredQuestions(result);
            surveyActivityPresenter.saveObjectToDisk(result, basePath);
            surveyActivityPresenter.getNextQuestionAndDirectToFragments();
        }
    };

    @Override
    public void onError(int code) {
        Log.i(Constants.LOG_TAG, "Error parsing the survey. Error code : " + code);
    }
}
