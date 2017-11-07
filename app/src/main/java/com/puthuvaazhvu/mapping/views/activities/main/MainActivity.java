package com.puthuvaazhvu.mapping.views.activities.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.puthuvaazhvu.mapping.DataInjection;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.PrefsStorage;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.activities.BaseActivity;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.ConformationQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.FragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.GridQuestionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.InfoFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.message.MessageQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.QuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.SingleQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridQuestionData;
import com.puthuvaazhvu.mapping.views.fragments.summary.SummaryFragment;
import com.puthuvaazhvu.mapping.views.helpers.FlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.next_flow.IFlow;
import com.puthuvaazhvu.mapping.views.helpers.next_flow.FlowImplementation;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentManagerInvoker;
import com.puthuvaazhvu.mapping.views.managers.commands.FragmentPopCommand;
import com.puthuvaazhvu.mapping.views.managers.commands.FragmentPushCommand;
import com.puthuvaazhvu.mapping.views.managers.commands.FragmentReplaceCommand;
import com.puthuvaazhvu.mapping.views.managers.receiver.StackFragmentManagerReceiver;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity extends BaseActivity
        implements Contract.View, FragmentCommunicationInterface {

    private StackFragmentManagerInvoker stackFragmentManagerInvoker;
    private StackFragmentManagerReceiver stackFragmentManagerReceiver;

    private Contract.UserAction presenter;

    private FlowHelper flowHelper;

    private IFlow iFlow;

    private ProgressDialog progressDialog;

    private PrefsStorage prefsStorage;

    private boolean defaultBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog();
        progressDialog.setCancelable(false);

        stackFragmentManagerInvoker = new StackFragmentManagerInvoker();
        stackFragmentManagerReceiver = new StackFragmentManagerReceiver(getSupportFragmentManager(), R.id.container);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
        prefsStorage = PrefsStorage.getInstance(sharedPreferences);

        Handler handler = new Handler(Looper.getMainLooper());

        presenter = new Presenter(
                this,
                DataInjection.provideSurveyDataRepository(handler, this),
                handler,
                SaveToFile.getInstance(),
                GetFromFile.getInstance()
        );

        String latestSurveyID = prefsStorage.getLatestSurveyID();

        if (latestSurveyID == null) {
            startListOfSurveysActivity();
        } else {
            presenter.loadSurvey(latestSurveyID);
        }
    }

    @Override
    public void onBackPressed() {
        if (defaultBackPressed)
            super.onBackPressed();
        else
            presenter.getPrevious();
    }

    @Override
    public void onSurveyLoaded(Survey survey) {

        if (survey.getQuestionList().isEmpty()) {
            onError(R.string.invalid_data);
            defaultBackPressed = true;
            return;
        }

        Question root = survey.getQuestionList().get(0);

        // set a mock answer for the root question
//        QuestionData rootData = QuestionData.adapter(root);
//
//        OptionData responseData = OptionData.adapter(root);
//
//        AnswerData answerData = new SingleAnswerData(root.getRawNumber(), root.getTextString(), null, "DUMMY_ANSWER", "0");
//        responseData.setAnswerData(answerData);
//
//        rootData.setResponseData(responseData);

        iFlow = new FlowImplementation(root);

        flowHelper = new FlowHelper(iFlow);

        presenter.initData(survey, flowHelper);

//        updateCurrentQuestion(rootData, new Runnable() {
//            @Override
//            public void run() {
//                // set the first question
//                presenter.getNext();
//            }
//        });

        presenter.getNext();
    }

    @Override
    public void onError(int messageID) {
        Utils.showMessageToast(getString(messageID), this);
    }

    @Override
    public void shouldShowGrid(QuestionData parent, ArrayList<GridQuestionData> question) {
        QuestionFragment fragment = GridQuestionsFragment.getInstance(parent, question);
        replaceFragmentCommand(fragment, parent.getSingleQuestion().getRawNumber());
        executePendingCommands();
    }

    @Override
    public void shouldShowSingleQuestion(QuestionData question) {
        QuestionFragment fragment = SingleQuestionFragment.getInstance(question);
        replaceFragmentCommand(fragment, question.getSingleQuestion().getRawNumber());
        executePendingCommands();
    }

    @Override
    public void shouldShowQuestionAsInfo(QuestionData question) {
        InfoFragment fragment = InfoFragment.getInstance(question);
        replaceFragmentCommand(fragment, question.getSingleQuestion().getRawNumber());
        executePendingCommands();
    }

    @Override
    public void shouldShowConformationQuestion(QuestionData question) {
        ConformationQuestionFragment fragment = ConformationQuestionFragment.getInstance(question);
        replaceFragmentCommand(fragment, question.getSingleQuestion().getRawNumber());
        executePendingCommands();
    }

    @Override
    public void shouldShowMessageQuestion(QuestionData question) {
        //Todo:
//        MessageQuestionFragment fragment = MessageQuestionFragment.getInstance(question);
//        replaceFragmentCommand(fragment, question.getSingleQuestion().getRawNumber());
//        executePendingCommands();
    }

    @Override
    public void shouldShowSummary(Survey survey) {
        SummaryFragment summaryFragment = SummaryFragment.getInstance(survey);
        replaceFragmentCommand(summaryFragment, "summary_fragment");
        executePendingCommands();
    }

    @Override
    public void onSurveySaved(Survey survey) {
        defaultBackPressed = true;
        presenter.updateAnswersInfoFile(survey);
        startListOfSurveysActivity();
    }

    @Override
    public void onSurveyEnd() {
        Timber.i("The survey is completed");
        defaultBackPressed = true;
        presenter.dumpSurveyToFile();
    }

    @Override
    public void remove(Question question) {
        addPopFragmentCommand(question.getRawNumber());
    }

    @Override
    public void remove(ArrayList<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            String tag = questions.get(i).getRawNumber();
            addPopFragmentCommand(tag);
        }
        executePendingCommands();
    }

    @Override
    public void showLoading(int messageID) {
        if (progressDialog.isVisible() || progressDialog.isAdded()) {
            progressDialog.dismiss();
        }
        progressDialog.setTextView(getString(messageID));
        progressDialog.show(getSupportFragmentManager(), "progress_dialog");
    }

    @Override
    public void hideLoading() {
        if (progressDialog.isVisible())
            progressDialog.dismiss();
    }

    @Override
    public void toggleDefaultBackPressed(boolean toggle) {
        defaultBackPressed = toggle;
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
            updateCurrentQuestion(questionData, new Runnable() {
                @Override
                public void run() {
                    getNextQuestion();
                }
            });
        }
    }

    @Override
    public void finishCurrentQuestion(final QuestionData questionData, boolean shouldLogOptions) {
        if (shouldLogOptions) {
            updateCurrentQuestion(questionData, new Runnable() {
                @Override
                public void run() {
                    presenter.finishCurrent(questionData);
                    presenter.getNext();
                }
            });
        } else {
            presenter.finishCurrent(questionData);
            presenter.getNext();
        }
    }

    @Override
    public void onBackPressedFromQuestion(QuestionData currentQuestionData) {
        presenter.getPrevious();
    }

    @Override
    public void onErrorWhileAnswering(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showMessageToast(message, MainActivity.this);
            }
        });
    }

    public void moveToQuestionAt(int index) {
        presenter.moveToQuestionAt(index);
    }

    public void updateCurrentQuestion(QuestionData questionData, Runnable runnable) {
        presenter.updateCurrentQuestion(questionData, runnable);
    }

    public void getNextQuestion() {
        presenter.getNext();
    }

    public void replaceFragmentCommand(Fragment fragment, String tag) {
        stackFragmentManagerInvoker.addCommand(new FragmentReplaceCommand(stackFragmentManagerReceiver, tag, fragment));
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

    private void startListOfSurveysActivity() {
        Intent intent = new Intent(this, SurveyListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void forceCrash() {
        throw new RuntimeException("This is a test crash");
    }

}
