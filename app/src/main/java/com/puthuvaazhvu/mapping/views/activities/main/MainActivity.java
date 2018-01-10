package com.puthuvaazhvu.mapping.views.activities.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.network.APIs;
import com.puthuvaazhvu.mapping.network.implementations.SingleSurveyAPI;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.other.RepeatingTask;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.PrefsStorage;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.ConfirmationQuestionCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.GridQuestionFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.QuestionDataFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.ShowTogetherQuestionCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.SingleQuestionFragmentCommunication;
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
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class MainActivity extends MenuActivity
        implements Contract.View,
        SingleQuestionFragmentCommunication,
        QuestionDataFragmentCommunication,
        GridQuestionFragmentCommunication,
        ConfirmationQuestionCommunication,
        ShowTogetherQuestionCommunication {

    private final long REPEATING_TASK_INTERVAL = TimeUnit.MINUTES.toMillis(30);

    private StackFragmentManagerInvoker stackFragmentManagerInvoker;
    private StackFragmentManagerReceiver stackFragmentManagerReceiver;

    private Contract.UserAction presenter;

    private FlowHelper flowHelper;

    private IFlow iFlow;

    private ProgressDialog progressDialog;

    private PrefsStorage prefsStorage;

    private boolean defaultBackPressed = false;

    private RepeatingTask repeatingTask;

    private boolean dumpSurveyRepeatingTask = true;

    private Handler handler;

    private DataFragment dataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataFragment = DataFragment.getInstance(getSupportFragmentManager());

        progressDialog = new ProgressDialog();
        // progressDialog.setCancelable(false);

        stackFragmentManagerInvoker = new StackFragmentManagerInvoker();
        stackFragmentManagerReceiver = new StackFragmentManagerReceiver(getSupportFragmentManager(), R.id.container);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
        prefsStorage = PrefsStorage.getInstance(sharedPreferences);

        handler = new Handler(Looper.getMainLooper());

        GetFromFile getFromFile = GetFromFile.getInstance();
        SingleSurveyAPI singleSurveyAPI = SingleSurveyAPI.getInstance(APIs.getAuth(sharedPreferences));
        String optionsJson = Utils.readFromAssetsFile(this, "options_fill.json");

        presenter = new MainPresenter(
                this,
                SurveyDataRepository.getInstance(getFromFile, sharedPreferences, singleSurveyAPI, optionsJson),
                handler,
                SaveToFile.getInstance(),
                GetFromFile.getInstance()
        );

        onSurveyLoaded(MappingApplication.globalContext.getApplicationData().getSurvey());

        repeatingTask = new RepeatingTask(handler, new Runnable() {
            @Override
            public void run() {
                if (dumpSurveyRepeatingTask) {
                    Timber.i("Automatic saving done.");
                    presenter.dumpSurveyToFile(false, true);
                }
            }
        }, REPEATING_TASK_INTERVAL, true);

        if (dumpSurveyRepeatingTask)
            repeatingTask.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Timber.i("save menu clicked");
                presenter.dumpSurveyToFile(false, true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

        if (survey == null || survey.getRootQuestion() == null) {
            onError(R.string.invalid_data);
            defaultBackPressed = true;
            return;
        }

        Question root = survey.getRootQuestion();

        if (MappingApplication.globalContext.getApplicationData().getSnapshotPath() != null) {
            iFlow = new FlowImplementation(root, MappingApplication.globalContext.getApplicationData().getSnapshotPath());
            flowHelper = new FlowHelper(iFlow);
            presenter.initData(survey, flowHelper);
            presenter.showCurrent();
        } else {
            iFlow = new FlowImplementation(root);
            flowHelper = new FlowHelper(iFlow);
            presenter.initData(survey, flowHelper);
            presenter.getNext();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        repeatingTask.stop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        dataFragment.setCurrentQuestion(presenter.getCurrent());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Question question = dataFragment.getCurrentQuestion();
        if (question != null) {
            presenter.setCurrent(question);
            presenter.showCurrent();
        }
    }

    @Override
    public void onError(int messageID) {
        Utils.showMessageToast(getString(messageID), this);
    }

    @Override
    public void loadQuestionUI(Fragment fragment, String tag) {
        replaceFragmentCommand(fragment, tag);
        executePendingCommands();
    }

    @Override
    public void shouldShowSummary(Survey survey) {
        SummaryFragment summaryFragment = SummaryFragment.getInstance(survey);
        replaceFragmentCommand(summaryFragment, "summary_fragment");
        executePendingCommands();
    }

    @Override
    public void onSurveySaved(Survey survey) {
        Timber.i("Survey saved successfully.");
        if (paused) {
            return;
        }

        // update application data here
    }

    @Override
    public void onSurveyEnd() {
        Timber.i("The survey is completed");
        defaultBackPressed = true;
        dumpSurveyRepeatingTask = false;
        repeatingTask.stop();
        presenter.dumpSurveyToFile(true, false);
    }

    @Override
    public void openListOfSurveysActivity() {
        if (paused) {
            return;
        }

        AlertDialog alertDialog = Utils.createAlertDialog(
                this,
                getString(R.string.survey_complete),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        defaultBackPressed = true;
                        startListOfSurveysActivity();
                    }
                }, null);
        alertDialog.setCancelable(false);

        if (!paused)
            alertDialog.show();
    }

    @Override
    public void showLoading(int messageID) {
        if (paused) {
            return;
        }

        if (progressDialog.isVisible() || progressDialog.isAdded()) {
            progressDialog.dismiss();
        }
        progressDialog.setTextView(getString(messageID));
        progressDialog.show(getSupportFragmentManager(), "progress_dialog");
    }

    @Override
    public void showMessage(int messageID) {
        Utils.showMessageToast(messageID, this);
    }

    @Override
    public void hideLoading() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isVisible())
                    progressDialog.dismiss();
            }
        });
    }

    @Override
    public void toggleDefaultBackPressed(boolean toggle) {
        defaultBackPressed = toggle;
    }

    public void moveToQuestionAt(int index) {
        presenter.moveToQuestionAt(index);
    }

    public void updateCurrentQuestion(Question question, ArrayList<Option> response, Runnable runnable) {
        presenter.updateCurrentQuestion(question, response, runnable);
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

    @Override
    public void onBackPressedFromGrid(Question question) {
        onBackPressedFromSingleQuestion(question);
    }

    @Override
    public void onNextPressedFromGrid(Question question) {
        presenter.finishCurrent(question);
        presenter.getNext();
    }

    @Override
    public void onQuestionSelectedFromGrid(Question question, int pos) {
        // if a question is clicked
        if (pos < 0) {
            throw new IllegalArgumentException("The position is invalid.");
        }

        moveToQuestionAt(pos);
    }

    @Override
    public Question getCurrentQuestionFromActivity() {
        return presenter.getCurrent();
    }

    @Override
    public void onNextPressedFromSingleQuestion(Question question, ArrayList<Option> response) {
        // normal stack flow
        updateCurrentQuestion(question, response, new Runnable() {
            @Override
            public void run() {
                getNextQuestion();
            }
        });
    }

    @Override
    public void onBackPressedFromSingleQuestion(Question question) {
        presenter.getPrevious();
    }

    @Override
    public void onError(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showMessageToast(message, MainActivity.this);
            }
        });
    }

    @Override
    public void onBackPressedFromConformationQuestion(final Question question, ArrayList<Option> response) {
        updateCurrentQuestion(question, response, new Runnable() {
            @Override
            public void run() {
                presenter.finishCurrent(question);
                presenter.getNext();
            }
        });
    }

    @Override
    public void onBackPressedFromShownTogetherQuestion(Question question) {
        onBackPressedFromSingleQuestion(question);
    }

    @Override
    public void onNextPressedFromShownTogetherQuestion(Question question) {
        presenter.finishCurrent(question);
        presenter.getNext();
    }
}
