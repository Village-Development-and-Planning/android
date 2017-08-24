package com.puthuvaazhvu.mapping.Survey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.QuestionTree.SingleQuestion.QuestionFragment;
import com.puthuvaazhvu.mapping.Question.QuestionTree.SingleQuestion.QuestionFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.R;

import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class SurveyActivity extends AppCompatActivity implements QuestionFragmentCommunicationInterface {
    String surveyJSON;
    QuestionFragment questionFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.survey_activity);

        surveyJSON = getIntent().getExtras().getString(Constants.IntentKeys.SurveyActivity_survey_data_string);
    }

    private void loadQuestionFragment(QuestionModal questionModal) {
        questionFragment = null;
        questionFragment = QuestionFragment.getInstance(questionModal);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.question_container, questionFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void moveToNextQuestion(QuestionModal currentQuestion, List<OptionData> optionDataList) {

    }

    @Override
    public void moveToPreviousQuestion(QuestionModal currentQuestion) {

    }
}
