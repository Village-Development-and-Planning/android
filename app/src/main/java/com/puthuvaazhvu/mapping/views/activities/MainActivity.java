package com.puthuvaazhvu.mapping.views.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.puthuvaazhvu.mapping.DataInjection;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.FragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.GridQuestions;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentImpl;
import com.puthuvaazhvu.mapping.views.managers.operation.CascadeOperation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements Contract.View, FragmentCommunicationInterface {

    private CascadeOperation cascadeOperation;
    private StackFragmentImpl stackFragment;
    private Contract.UserAction presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stackFragment = new StackFragmentImpl(getSupportFragmentManager());
        cascadeOperation = new CascadeOperation(stackFragment);

        presenter = new Presenter(this, DataInjection.provideSurveyDataRepository());
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
    public void shouldShowSingleQuestion(Question question) {

    }

    @Override
    public void remove(Question question) {

    }

    @Override
    public void remove(ArrayList<Question> questions) {

    }

    @Override
    public void onQuestionAnswered(Data data) {

    }

    @Override
    public void onBackPressedFromQuestion(Data currentData) {

    }
}
