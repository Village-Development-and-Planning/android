package com.puthuvaazhvu.mapping.Survey;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Modals.Survey;
import com.puthuvaazhvu.mapping.Question.Loop.QuestionTreeRootLoopFragment;
import com.puthuvaazhvu.mapping.Question.Loop.QuestionTreeRootLoopFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragment;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.DataHelper;
import com.puthuvaazhvu.mapping.utils.StorageHelpers;

import java.util.HashMap;

import static com.puthuvaazhvu.mapping.Constants.DEBUG;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class SurveyActivity extends AppCompatActivity
        implements SurveyActivityCommunicationInterface, SurveyActivityUICommunicationInterface {
    String surveyJSON;
    SurveyActivityPresenter surveyActivityPresenter;
    QuestionTreeFragment questionTreeFragment;
    QuestionTreeRootLoopFragment questionTreeRootLoopFragment;
    String basePath;
    TextView infoTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.survey_activity);

        infoTxt = (TextView) findViewById(R.id.info_text);

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE};

        StorageHelpers.isPermissionGranted(this, permissions);

        basePath = getFilesDir().getAbsolutePath();

        if (DEBUG) {
            surveyJSON = DataHelper.readFromAssetsFile(this, "test_data/survey_3.json");
        } else {
            surveyJSON = getIntent().getExtras().getString(Constants.IntentKeys.SurveyActivity_survey_data_string);
        }

        surveyActivityPresenter = new SurveyActivityPresenter(this);
        surveyActivityPresenter.parseSurveyJson(surveyJSON);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Data will not be saved unless the storage permission is granted."
                    , Toast.LENGTH_SHORT).show();
        }
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

        @Override
        public void onChildFragmentPop() {

        }
    };

    private QuestionTreeRootLoopFragmentCommunicationInterface questionTreeRootLoopFragmentCommunicationInterface =
            new QuestionTreeRootLoopFragmentCommunicationInterface() {
                @Override
                public void onLoopFinished(HashMap<String, HashMap<String, QuestionModal>> result) {
                    surveyActivityPresenter.logAnsweredQuestions(result);
                    surveyActivityPresenter.getNextQuestionAndDirectToFragments();
                }
            };

    @Override
    public void onError(int code) {
        Log.i(Constants.LOG_TAG, "Error parsing the survey. Error code : " + code);
    }

    @Override
    public void setInfoText(String text) {
        infoTxt.setText(text);
    }
}
