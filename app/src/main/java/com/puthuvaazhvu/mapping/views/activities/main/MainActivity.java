package com.puthuvaazhvu.mapping.views.activities.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
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
import com.puthuvaazhvu.mapping.views.activities.BaseDataActivity;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListActivity;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.ConformationQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.FragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.GridQuestionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.InfoFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.MessageQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.fragment.together.TogetherQuestionFragment;
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
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class MainActivity extends BaseDataActivity
        implements Contract.View,
        FragmentCommunicationInterface {

    private final long REPEATING_TASK_INTERVAL = TimeUnit.MINUTES.toMillis(30);

    private StackFragmentManagerInvoker stackFragmentManagerInvoker;
    private StackFragmentManagerReceiver stackFragmentManagerReceiver;

    private Contract.UserAction presenter;

    private FlowHelper flowHelper;

    private IFlow iFlow;

    private ProgressDialog progressDialog;

    private PrefsStorage prefsStorage;

    private boolean defaultBackPressed = false;

    private BroadcastReceiver dataDumpAckBroadcast;

    private RepeatingTask repeatingTask;

    private boolean dumpSurveyRepeatingTask = true;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

//        String latestSurveyID = prefsStorage.getLatestSurveyID();
//
//        if (latestSurveyID == null) {
//            startListOfSurveysActivity();
//        } else {
//            presenter.loadSurvey(latestSurveyID);
//        }

        onSurveyLoaded(applicationData.getSurvey());

        repeatingTask = new RepeatingTask(handler, new Runnable() {
            @Override
            public void run() {
                if (dumpSurveyRepeatingTask) {
                    Timber.i("Automatic saving done.");
                    presenter.dumpSurveyToFile(false);
                }
            }
        }, REPEATING_TASK_INTERVAL);

        if (dumpSurveyRepeatingTask)
            repeatingTask.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Timber.i("save menu clicked");
                presenter.dumpSurveyToFile(false);
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

        // set the global application data
        // applicationData.setSurvey(survey);

        Question root = survey.getRootQuestion();

        if (applicationData.getSnapshotPath() != null) {
            iFlow = new FlowImplementation(root, applicationData.getSnapshotPath());
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

        registerDataDumpAckBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterDataDumpAckBroadcast();
        repeatingTask.stop();
    }

    private void registerDataDumpAckBroadcast() {
        IntentFilter intentFilter = new IntentFilter(Constants.INTENT_FILTERS.DUMP_SERVICE_END_BROADCAST);

        dataDumpAckBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.i("Data dump ack broadcast received");
                boolean err = intent.getExtras().getBoolean("err_status");
                hideLoading();
                if (err) {
                    Timber.e(intent.getExtras().getString("msg"));
                    onError(R.string.error_saving_survey);
                } else {
                    showMessage(R.string.save_successful);
                }
            }
        };

        registerReceiver(dataDumpAckBroadcast, intentFilter);
    }

    private void unregisterDataDumpAckBroadcast() {
        unregisterReceiver(dataDumpAckBroadcast);
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
        MessageQuestionFragment fragment = MessageQuestionFragment.getInstance(question);
        replaceFragmentCommand(fragment, question.getSingleQuestion().getRawNumber());
        executePendingCommands();
    }

    @Override
    public void shouldShowTogetherQuestion(Question currentReference, QuestionData question) {
        TogetherQuestionFragment fragment = TogetherQuestionFragment.getInstance(currentReference, question);
        replaceFragmentCommand(fragment, question.getSingleQuestion().getRawNumber());
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
    }

    @Override
    public void onSurveyEnd() {
        Timber.i("The survey is completed");
        defaultBackPressed = true;
        dumpSurveyRepeatingTask = false;
        repeatingTask.stop();
        presenter.dumpSurveyToFile(true);
    }

    @Override
    public void openListOfSurveysActivity() {
        defaultBackPressed = true;
        startListOfSurveysActivity();
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
