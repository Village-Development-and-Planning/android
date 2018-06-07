package com.puthuvaazhvu.mapping.views.activities.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.other.RepeatingTask;
import com.puthuvaazhvu.mapping.utils.DialogHandler;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.activities.MenuActivity;
import com.puthuvaazhvu.mapping.views.activities.home.HomeActivity;
import com.puthuvaazhvu.mapping.views.activities.modals.CurrentSurveyInfo;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.GridQuestionFragmentCallbacks;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.QuestionFragmentCallbacks;
import com.puthuvaazhvu.mapping.views.fragments.question.types.QuestionFragmentTypes;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends MenuActivity
        implements Contract.View, QuestionFragmentCallbacks, GridQuestionFragmentCallbacks {

    private final long REPEATING_TASK_INTERVAL = TimeUnit.MINUTES.toMillis(30);

    private Contract.UserAction presenter;

    private FlowLogic flowLogic;

    private boolean defaultBackPressed = false;

    private RepeatingTask repeatingTask;

    private boolean dumpSurveyRepeatingTask = true;

    private Handler handler;

    SharedPreferences sharedPreferences;

    DialogHandler dialogHandler;

    ProgressDialog progressDialog;

    MainActivityViewModal viewModal;

    CurrentSurveyInfo surveyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewModal = ViewModelProviders.of(this).get(MainActivityViewModal.class);

        progressDialog = new ProgressDialog();
        progressDialog.setCancelable(false);

        dialogHandler = new DialogHandler(progressDialog, getSupportFragmentManager());

        sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);

        handler = new Handler(Looper.getMainLooper());

        repeatingTask = new RepeatingTask(handler, new Runnable() {
            @Override
            public void run() {
                if (dumpSurveyRepeatingTask) {
                    Timber.i("Automatic saving done.");
                    presenter.dumpSnapshot();
                }
            }
        }, REPEATING_TASK_INTERVAL, true);

        if (dumpSurveyRepeatingTask)
            repeatingTask.start();

        surveyInfo = getIntent().getExtras().getParcelable("survey_list_data");

        presenter = new MainPresenter(this, handler, surveyInfo);

        Survey survey = viewModal.getSurvey().getValue();
        FlowLogic flowLogic = viewModal.getFlowLogic();

        if (survey == null || flowLogic == null) {
            showLoading(R.string.loading);

            presenter.init()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<FlowLogic>() {
                                   @Override
                                   public void accept(FlowLogic flowLogic) throws Exception {
                                       hideLoading();
                                       viewModal.setSurveyMutableLiveData(presenter.getSurvey());
                                       viewModal.setFlowLogic(flowLogic);
                                       presenter.getNext();
                                   }
                               },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    hideLoading();
                                    finishActivityWithError(throwable.getMessage());
                                }
                            });
        } else {
            presenter.setSurvey(survey);
            presenter.setFlowLogic(flowLogic);
        }
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onError(int messageID) {
        Utils.showMessageToast(getString(messageID), this);
    }

    @Override
    public void updateCurrentQuestion(Question question) {
        viewModal.setCurrentQuestion(question);
    }

    @Override
    public void loadQuestionUI(Fragment fragment, String tag) {
        replaceFragment(fragment, tag);
    }

    @Override
    public void finishActivityWithError(String error) {
        Utils.showMessageToast(error, this);
        finish();
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
    public void showSurveyCompleteDialog() {
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
                        startHomeActivity();
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

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void forceCrash() {
        throw new RuntimeException("This is a test crash");
    }

    @Override
    public void onNextPressed(QuestionFragmentTypes type, ArrayList<Option> response) {
        Question currentQuestion = viewModal.getCurrentQuestion();

        switch (type) {
            case GPS:
            case INFO:
            case CONFORMATION:
            case SINGLE:
            case MESSAGE:
                presenter.updateCurrentQuestion(response, new Runnable() {
                    @Override
                    public void run() {
                        presenter.getNext();
                    }
                });
                break;
            case SHOWN_TOGETHER:
            case GRID:
                presenter.finishCurrent(currentQuestion);
                break;
        }
    }

    @Override
    public void onBackPressed(QuestionFragmentTypes type, Object... args) {
        final Question currentQuestion = viewModal.getCurrentQuestion();

        switch (type) {
            case GPS:
                presenter.getPrevious();
                break;
            case GRID:
            case MESSAGE:
            case INFO:
            case SINGLE:
            case SHOWN_TOGETHER:
                presenter.getPrevious();
                break;
            case CONFORMATION:
                presenter.updateCurrentQuestion((ArrayList<Option>) args[0], new Runnable() {
                    @Override
                    public void run() {
                        presenter.finishCurrent(currentQuestion);
                    }
                });
                break;
        }
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
    public void onGridItemClicked(int pos) {
        presenter.moveToQuestionAt(pos);
    }
}
