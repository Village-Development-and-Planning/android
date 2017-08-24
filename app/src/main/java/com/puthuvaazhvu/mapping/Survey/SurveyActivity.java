package com.puthuvaazhvu.mapping.Survey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Question.SingleQuestion.QuestionFragment;
import com.puthuvaazhvu.mapping.R;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class SurveyActivity extends AppCompatActivity {
    String surveyJSON;
    QuestionFragment questionFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.survey_activity);

        surveyJSON = getIntent().getExtras().getString(Constants.IntentKeys.SurveyActivity_survey_data_string);
    }
}
