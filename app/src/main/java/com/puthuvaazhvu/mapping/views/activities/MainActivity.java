package com.puthuvaazhvu.mapping.views.activities;

import android.support.v4.app.Fragment;
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
import com.puthuvaazhvu.mapping.views.helpers.FlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.FlowImplementation;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentManagerInvoker;
import com.puthuvaazhvu.mapping.views.managers.commands.FragmentPopCommand;
import com.puthuvaazhvu.mapping.views.managers.commands.FragmentPushCommand;
import com.puthuvaazhvu.mapping.views.managers.receiver.StackFragmentManagerReceiver;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements Contract.View, FragmentCommunicationInterface {

    private StackFragmentManagerInvoker stackFragmentManagerInvoker;
    private StackFragmentManagerReceiver stackFragmentManagerReceiver;

    private Contract.UserAction presenter;

    private FlowHelper flowHelper;
    private FlowImplementation flowImplementation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stackFragmentManagerInvoker = new StackFragmentManagerInvoker();
        stackFragmentManagerReceiver = new StackFragmentManagerReceiver(getSupportFragmentManager(), R.id.container);

        presenter = new Presenter(this, DataInjection.provideSurveyDataRepository(this));
        presenter.loadSurvey();
    }

    @Override
    public void onSurveyLoaded(Survey survey) {
        Question root = survey.getQuestionList().get(0);

        // set a mock answer for the root question
        QuestionData rootData = QuestionData.adapter(root);

        OptionData responseData = OptionData.adapter(root);

        AnswerData answerData = new SingleAnswerData(root.getId(), root.getTextString(), null, "DUMMY_ANSWER", "0");
        responseData.setAnswerData(answerData);

        rootData.setResponseData(responseData);

        flowImplementation = new FlowImplementation(root);
        flowHelper = new FlowHelper(flowImplementation);
        presenter.initData(survey, flowHelper);

        updateCurrentQuestion(rootData);

        // set the first question
        presenter.getNext();
    }

    @Override
    public void onError(String message) {
        Utils.showErrorMessage(message, this);
    }

    @Override
    public void shouldShowGrid(QuestionData parent, ArrayList<GridQuestionData> question) {
        QuestionFragment fragment = GridQuestionsFragment.getInstance(parent, question);
        addPushFragmentCommand(fragment, parent.getSingleQuestion().getId());
        executePendingCommands();
    }

    @Override
    public void shouldShowSingleQuestion(QuestionData question) {
        QuestionFragment fragment = SingleQuestionFragment.getInstance(question);
        addPushFragmentCommand(fragment, question.getSingleQuestion().getId());
        executePendingCommands();
    }

    @Override
    public void shouldShowQuestionAsInfo(QuestionData question) {
        InfoFragment fragment = InfoFragment.getInstance(question);
        addPushFragmentCommand(fragment, question.getSingleQuestion().getId());
        executePendingCommands();
    }

    @Override
    public void shouldShowConformationQuestion(QuestionData question) {
        ConformationQuestionFragment fragment = ConformationQuestionFragment.getInstance(question);
        addPushFragmentCommand(fragment, question.getSingleQuestion().getId());
        executePendingCommands();
    }

    @Override
    public void onSurveyEnd() {

    }

    @Override
    public void remove(Question question) {
        addPopFragmentCommand(question.getId());
    }

    @Override
    public void remove(ArrayList<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            String tag = questions.get(i).getId();
            addPopFragmentCommand(tag);
        }
        executePendingCommands();
    }

    @Override
    public void onQuestionAnswered(QuestionData questionData, boolean isNewRoot) {
        if (isNewRoot) {
            // if a question is clicked
            int position = questionData.getPosition();

            if (position < 0) {
                throw new IllegalArgumentException("The position is invalid.");
            }

            moveToQuestionAt(position);

        } else {
            // normal stack flow
            updateCurrentQuestion(questionData);
            getNextQuestion();
        }
    }

    @Override
    public void finishCurrentQuestion(QuestionData questionData, boolean shouldLogOptions) {
        if (shouldLogOptions) {
            updateCurrentQuestion(questionData);
        }
        presenter.finishCurrent(questionData);
        presenter.getNext();
    }

    @Override
    public void onBackPressedFromQuestion(QuestionData currentQuestionData) {
        // TODO: error ID mismatch
//        addPopFragmentCommand(currentQuestionData.getSingleQuestion().getId());
//        executePendingCommands();
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

    public void moveToQuestionAt(int index) {
        presenter.moveToQuestionAt(index);
    }

    public void updateCurrentQuestion(QuestionData questionData) {
        presenter.updateCurrentQuestion(questionData);
    }

    public void getNextQuestion() {
        presenter.getNext();
    }

    public void addPushFragmentCommand(Fragment fragment, String tag) {
        stackFragmentManagerInvoker.addCommand(new FragmentPushCommand(stackFragmentManagerReceiver, tag, fragment));
    }

    public void addPopFragmentCommand(String tag) {
        // don't pop if the stack count is 1.
        if (stackFragmentManagerReceiver.getStackCount() == 1) {
            Timber.e("There is only one fragment in the stack.");
            return;
        }
        stackFragmentManagerInvoker.addCommand(new FragmentPopCommand(stackFragmentManagerReceiver, tag));
    }

    public void executePendingCommands() {
        stackFragmentManagerInvoker.executeCommand();
    }
}
