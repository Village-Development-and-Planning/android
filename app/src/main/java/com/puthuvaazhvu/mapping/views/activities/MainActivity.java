package com.puthuvaazhvu.mapping.views.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.puthuvaazhvu.mapping.DataInjection;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.ConformationQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.FragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.GridQuestionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.InfoFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.QuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.SingleQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridData;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentManager;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentManagerImpl;
import com.puthuvaazhvu.mapping.views.managers.operation.CascadeOperation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements Contract.View, FragmentCommunicationInterface {

    private CascadeOperation cascadeOperation;
    private StackFragmentManager stackFragmentManager;
    private Contract.UserAction presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stackFragmentManager = new StackFragmentManagerImpl(getSupportFragmentManager(), R.id.container);
        cascadeOperation = new CascadeOperation(stackFragmentManager);

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
        QuestionFragment fragment = GridQuestionsFragment.getInstance(question);
        cascadeOperation.pushOperation(tag, fragment);
    }

    @Override
    public void shouldShowSingleQuestion(Data question) {
        QuestionFragment fragment = SingleQuestionFragment.getInstance(question);
        cascadeOperation.pushOperation(question.getQuestion().getId(), fragment);
    }

    @Override
    public void shouldShowQuestionAsInfo(Data question) {
        InfoFragment fragment = InfoFragment.getInstance(question);
        cascadeOperation.pushOperation(question.getQuestion().getId(), fragment);
    }

    @Override
    public void shouldShowConformationQuestion(Data question) {
        ConformationQuestionFragment fragment = ConformationQuestionFragment.getInstance(question);
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
            setCurrentQuestion(data);
        } else {
            // normal stack flow
            if (shouldLogOption)
                updateCurrentQuestion(data);
            getNextQuestion();
        }
    }

    public void setCurrentQuestion(Data data) {
        presenter.setCurrentQuestion(data);
    }

    public void updateCurrentQuestion(Data data) {
        presenter.updateCurrentQuestion(data);
    }

    public void getNextQuestion() {
        presenter.getNext();
    }

    @Override
    public void onBackPressedFromQuestion(Data currentData) {
        // TODO:
    }

    @Override
    public void onErrorWhileAnswering(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showErrorMessage(message, MainActivity.this);
            }
        });
    }
}
