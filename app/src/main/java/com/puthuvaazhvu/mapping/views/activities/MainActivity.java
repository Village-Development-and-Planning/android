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
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.SingleQuestion;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridData;
import com.puthuvaazhvu.mapping.views.managers.StackFragment;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentImpl;
import com.puthuvaazhvu.mapping.views.managers.operation.CascadeOperation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements Contract.View, FragmentCommunicationInterface {

    private CascadeOperation cascadeOperation;
    private StackFragment stackFragment;
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
        presenter.startSurvey(survey);
    }

    @Override
    public void onError(String message) {
        Utils.showErrorMessage(message, this);
    }

    @Override
    public void shouldShowGrid(String tag, ArrayList<GridData> question) {
        com.puthuvaazhvu.mapping.views.fragments.question.fragment.Question fragment = GridQuestions.getInstance(question);
        cascadeOperation.pushOperation(tag, fragment);
    }

    @Override
    public void shouldShowSingleQuestion(Data question) {
        com.puthuvaazhvu.mapping.views.fragments.question.fragment.Question fragment = SingleQuestion.getInstance(question);
        cascadeOperation.pushOperation(question.getQuestion().getId(), fragment);
    }

    @Override
    public void remove(Question question) {
        cascadeOperation.popOperation(question.getId());
    }

    @Override
    public void remove(ArrayList<Question> questions) {
        String tags[] = new String[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            tags[i] = questions.get(i).getId();
        }
        cascadeOperation.popManyOperation(tags);
    }

    @Override
    public void onQuestionAnswered(Data data, boolean isNewRoot, boolean shouldLogOption) {
        if (isNewRoot) {
            // if a question is clicked
            presenter.setCurrentQuestion(data);
        } else {
            // normal stack flow
            presenter.updateCurrentQuestion(data);
            presenter.getNext();
        }
    }

    @Override
    public void onBackPressedFromQuestion(Data currentData) {
        // TODO:
    }
}
