package com.puthuvaazhvu.mapping.views.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements Contract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onSurveyLoaded(Survey survey) {

    }

    @Override
    public void onError(String message) {
        Utils.showErrorMessage(message, this);
    }

    @Override
    public void shouldShowGrid(Question question) {

    }

    @Override
    public void shouldShowQuestion(Question question) {

    }

    @Override
    public void remove(Question question) {

    }

    @Override
    public void remove(ArrayList<Question> questions) {

    }
}
