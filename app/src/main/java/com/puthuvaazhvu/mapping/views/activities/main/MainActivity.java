package com.puthuvaazhvu.mapping.views.activities.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.other.RepeatingTask;
import com.puthuvaazhvu.mapping.utils.DialogHandler;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogicImplementation;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.BaseQuestionFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.ConfirmationQuestionCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.GridQuestionFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.QuestionDataFragmentCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.ShowTogetherQuestionCommunication;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.SingleQuestionFragmentCommunication;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class MainActivity extends MenuActivity
        implements Contract.View,
        SingleQuestionFragmentCommunication,
        QuestionDataFragmentCommunication,
        GridQuestionFragmentCommunication,
        ConfirmationQuestionCommunication,
        ShowTogetherQuestionCommunication,
        BaseQuestionFragmentCommunication {

    private final long REPEATING_TASK_INTERVAL = TimeUnit.MINUTES.toMillis(30);

    private Contract.UserAction presenter;

    private FlowLogic flowLogic;

    private boolean defaultBackPressed = false;

    private RepeatingTask repeatingTask;

    private boolean dumpSurveyRepeatingTask = true;

    private Handler handler;

    private DataFragment dataFragment;

    SharedPreferences sharedPreferences;

    DialogHandler dialogHandler;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog();
        progressDialog.setCancelable(false);

        dialogHandler = new DialogHandler(progressDialog, getSupportFragmentManager());

        dataFragment = DataFragment.getInstance(getSupportFragmentManager());

        sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);

        handler = new Handler(Looper.getMainLooper());

        presenter = new MainPresenter(
                this,
                handler,
                sharedPreferences
        );

        repeatingTask = new RepeatingTask(handler, new Runnable() {
            @Override
            public void run() {
                if (dumpSurveyRepeatingTask) {
                    Timber.i("Automatic saving done.");
                    presenter.dumpSnapshot();
                }
            }
        }, REPEATING_TASK_INTERVAL, true);

        if (savedInstanceState != null) {
            String snapshot = dataFragment.getSnapshot();
            if (snapshot != null) {
                MappingApplication.globalContext.getApplicationData().setSurveySnapShotPath(snapshot);
            }
        }

        onSurveyLoaded(MappingApplication.globalContext.getApplicationData().getSurvey());

        if (dumpSurveyRepeatingTask)
            repeatingTask.start();
    }

    @Override
    public PauseHandler getPauseHandler() {
        return dialogHandler;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Timber.i("save menu clicked");
                presenter.dumpSnapshot();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        if (defaultBackPressed)
//            super.onBackPressed();
//        else
//            presenter.getPrevious();
    }

    @Override
    public void onSurveyLoaded(Survey survey) {

        if (survey == null || survey.getQuestion() == null) {
            onError(R.string.invalid_data);
            defaultBackPressed = true;
            return;
        }

        Question root = survey.getQuestion();

        flowLogic = new FlowLogicImplementation(
                root,
                sharedPreferences
        );
        flowLogic.setAuthJson(MappingApplication.globalContext.getApplicationData().getAuthJson());

        presenter.initData(survey, flowLogic);

        String snapShotPath = MappingApplication.globalContext.getApplicationData().getSurveySnapShotPath();

        if (snapShotPath == null)
            presenter.getNext();
        else {
            // from saved state
            // move to the current question and update the UI.

            Question question = QuestionUtils.moveToQuestionUsingPath(snapShotPath, root);

            // if the answers are from snapshot set this to the current question
            flowLogic.setCurrent(question);

            FlowLogic.FlowData flowData = flowLogic.getCurrent();
            loadQuestionUI(flowData.getFragment(), flowData.getQuestion().getNumber());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        dataFragment.setSnapshot(TextUtils.join(",", QuestionUtils.getPathOfQuestion(presenter.getCurrent())));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        String snapshot = dataFragment.getSnapshot();
//        if (snapshot != null) {
//            MappingApplication.globalContext.getApplicationData().setSurveySnapShotPath(snapshot);
//        }
    }

    @Override
    public void onError(int messageID) {
        Utils.showMessageToast(getString(messageID), this);
    }

    @Override
    public void loadQuestionUI(Fragment fragment, String tag) {
        replaceFragment(fragment, tag);
    }

    @Override
    public void shouldShowSummary(Survey survey) {
        throw new UnsupportedOperationException("Not implemented");
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
        presenter.dumpAnswer();
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
        progressDialog.setTextView(getString(messageID));
        dialogHandler.showDialog("progress_dialog");
    }

    @Override
    public void showMessage(int messageID) {
        Utils.showMessageToast(messageID, this);
    }

    @Override
    public void hideLoading() {
        dialogHandler.hideDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // pass gps result to gps framgnet
        Fragment frg = getSupportFragmentManager().findFragmentByTag("gps");
        if (frg != null) {
            frg.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void toggleDefaultBackPressed(boolean toggle) {
        defaultBackPressed = toggle;
    }

    public void moveToQuestionAt(int index) {
        presenter.moveToQuestionAt(index);
    }

    public void updateCurrentQuestion(Question question, ArrayList<Option> response, Runnable runnable) {
        presenter.updateCurrentQuestion(response, runnable);
    }

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
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
        //presenter.getNext();
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
                presenter.getNext();
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
                //presenter.getNext();
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
        //presenter.getNext();
    }

    @Override
    public FlowLogic getFlowLogic() {
        return flowLogic;
    }
}
