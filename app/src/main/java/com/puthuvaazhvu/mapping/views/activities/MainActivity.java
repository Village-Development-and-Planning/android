package com.puthuvaazhvu.mapping.views.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.puthuvaazhvu.mapping.DataInjection;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.AnswerData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.ConformationQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.FragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.GridQuestionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.InfoFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.QuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.SingleQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridQuestionData;
import com.puthuvaazhvu.mapping.views.helpers.flow.FlowHelperBase;
import com.puthuvaazhvu.mapping.views.helpers.flow.SurveyFlowHelper;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentManager;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentManagerImpl;
import com.puthuvaazhvu.mapping.views.managers.operation.CascadeOperation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements Contract.View, FragmentCommunicationInterface {

    private CascadeOperation cascadeOperation;
    private StackFragmentManager stackFragmentManager;
    private Contract.UserAction presenter;

    private FlowHelperBase flowHelperBase;

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
        Question root = survey.getQuestionList().get(0);

        // set a mock answer for the root question
        QuestionData rootData = QuestionData.adapter(root);

        OptionData responseData = OptionData.adapter(root);

        AnswerData answerData = new SingleAnswerData(root.getId(), root.getTextString(), null, "DUMMY", "0");
        responseData.setAnswerData(answerData);

        rootData.setResponseData(responseData);
        updateCurrentQuestion(rootData);

        flowHelperBase = new SurveyFlowHelper(root);
        presenter.startSurvey(survey, flowHelperBase);
    }

    @Override
    public void onError(String message) {
        Utils.showErrorMessage(message, this);
    }

    @Override
    public void shouldShowGrid(String tag, ArrayList<GridQuestionData> question) {
        QuestionFragment fragment = GridQuestionsFragment.getInstance(question);
        cascadeOperation.pushOperation(tag, fragment);
    }

    @Override
    public void shouldShowSingleQuestion(QuestionData question) {
        QuestionFragment fragment = SingleQuestionFragment.getInstance(question);
        cascadeOperation.pushOperation(question.getSingleQuestion().getId(), fragment);
    }

    @Override
    public void shouldShowQuestionAsInfo(QuestionData question) {
        InfoFragment fragment = InfoFragment.getInstance(question);
        cascadeOperation.pushOperation(question.getSingleQuestion().getId(), fragment);
    }

    @Override
    public void shouldShowConformationQuestion(QuestionData question) {
        ConformationQuestionFragment fragment = ConformationQuestionFragment.getInstance(question);
        cascadeOperation.pushOperation(question.getSingleQuestion().getId(), fragment);
    }

    @Override
    public void onSurveyEnd() {

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
    public void onQuestionAnswered(QuestionData questionData, boolean isNewRoot, boolean shouldLogOption) {
        if (isNewRoot) {
            // if a question is clicked
            int position = questionData.getPosition();

            if (position < 0) {
                throw new IllegalArgumentException("The position is invalid.");
            }

            moveToQuestionAt(position);

        } else {
            // normal stack flow
            if (shouldLogOption)
                updateCurrentQuestion(questionData);
            getNextQuestion();
        }
    }

    public void moveToQuestionAt(int index) {
        presenter.moveToQuestionAt(index);
    }

    public void updateCurrentQuestion(QuestionData questionData) {
        presenter.updateCurrentQuestion(questionData);
    }

    public void getNextQuestion() {
        presenter.getNext();
    }

    @Override
    public void onBackPressedFromQuestion(QuestionData currentQuestionData) {
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
