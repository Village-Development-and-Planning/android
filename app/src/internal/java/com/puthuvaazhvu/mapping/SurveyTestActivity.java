package com.puthuvaazhvu.mapping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.deserialization.SurveyGsonAdapter;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.other.RepeatingTask;
import com.puthuvaazhvu.mapping.utils.DialogHandler;
import com.puthuvaazhvu.mapping.utils.PauseHandler;
import com.puthuvaazhvu.mapping.utils.SharedPreferenceUtils;
import com.puthuvaazhvu.mapping.utils.Utils;
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
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.Util;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 24/02/18.
 */

public class SurveyTestActivity extends AppCompatActivity
        implements
        SingleQuestionFragmentCommunication,
        QuestionDataFragmentCommunication,
        GridQuestionFragmentCommunication,
        ConfirmationQuestionCommunication,
        ShowTogetherQuestionCommunication,
        BaseQuestionFragmentCommunication {

    private final int AUTOMATIC_DELAY = 100;

    FlowLogic flowLogic;
    Survey survey;
    Question root;
    String surveyFilename;
    Random random;

    boolean resumed = false;

    boolean automated = false;

    Handler handler;

    SharedPreferences sharedPreferences;

    RepeatingTask repeatingTask;

    DialogHandler dialogHandler;

    ProgressDialog progressDialog;

    Button startButton;
    Button stopButton;
    Button restartBtn;

    Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.survey_test);

        random = new Random();

        handler = new Handler();

        progressDialog = new ProgressDialog();

        dialogHandler = new DialogHandler(progressDialog, getSupportFragmentManager());

        surveyFilename = getIntent().getExtras().getString("file_name");

        sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);

        startButton = findViewById(R.id.start_btn);
        stopButton = findViewById(R.id.stop_btn);
        stopButton.setEnabled(false);

        restartBtn = findViewById(R.id.restart_btn);
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSurveyData();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (automated) {
                    return;
                }

                startAutomatic();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!automated) {
                    return;
                }

                stopAutomatic();
            }
        });

        repeatingTask = new RepeatingTask(handler,
                new Runnable() {
                    @Override
                    public void run() {
                        getNextAutomatic();
                    }
                },
                AUTOMATIC_DELAY,
                false);

        getSurveyData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialogHandler.resume();
        resumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        dialogHandler.pause();
        resumed = false;
    }

    @Override
    public void onBackPressedFromGrid(Question question) {
        Utils.showMessageToast("Not implemented", this);
    }

    @Override
    public void onNextPressedFromGrid(Question question) {
        loadFragment(flowLogic.finishCurrentAndGetNext());
    }

    @Override
    public void onQuestionSelectedFromGrid(Question question, int pos) {
        loadFragment(flowLogic.moveToIndexInChild(pos));
    }

    @Override
    public void onBackPressedFromShownTogetherQuestion(Question question) {
        Utils.showMessageToast("Not implemented", this);
    }

    @Override
    public void onNextPressedFromShownTogetherQuestion(Question question) {
        loadFragment(flowLogic.finishCurrentAndGetNext());
    }

    @Override
    public FlowLogic getFlowLogic() {
        return flowLogic;
    }

    @Override
    public Question getCurrentQuestionFromActivity() {
        return flowLogic.getCurrent().getQuestion();
    }

    @Override
    public void onBackPressedFromConformationQuestion(Question question, ArrayList<Option> response) {
        loadFragment(flowLogic.finishCurrentAndGetNext());
    }

    @Override
    public void onNextPressedFromSingleQuestion(Question question, ArrayList<Option> response) {
        flowLogic.update(response);
        loadFragment(flowLogic.getNext());
    }

    @Override
    public void onBackPressedFromSingleQuestion(Question question) {
        Utils.showMessageToast("Not implemented", this);
    }

    @Override
    public void onError(String message) {
        Timber.e(message);
    }

    private void loadFragment(FlowLogic.FlowData flowData) {
        if (flowData == null) {
            Utils.showMessageToast("Survey Over!", SurveyTestActivity.this);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopAutomatic();
                }
            }, AUTOMATIC_DELAY / 2);
            removeAllFragments();
            startButton.setEnabled(false);
            return;
        }

        if (!resumed) {
            return;
        }

        Timber.i("Current Fragment count " + getSupportFragmentManager().getFragments().size());

        currentFragment = flowData.getFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, currentFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void getNextAutomatic() {
        Question question = flowLogic.getCurrent().getQuestion();
        int optionsCount = question.getOptions().size();

        // add mock answer
        ArrayList<Option> response = new ArrayList<>();
        switch (question.getFlowPattern().getQuestionFlow().getUiMode()) {
            case NONE:
            case DUMMY:
                response.add(new Option());
                break;
            case GPS:
            case INPUT:
            case INFO:
            case MESSAGE:
            case CONFIRMATION:
                String position = "" + random.nextInt(2);
                Option option = new Option();
                option.setPosition(position);
                option.setValue(position);
                response.add(option);
                break;
            case SINGLE_CHOICE:
                int index = random.nextInt(optionsCount);
                response.add(question.getOptions().get(index));
                break;
            case MULTIPLE_CHOICE:
                int randomValue = random.nextInt(optionsCount);
                for (int i = 0; i < randomValue; i++) {
                    response.add(question.getOptions().get(i));
                }
                break;
        }

        flowLogic.update(response);

        loadFragment(flowLogic.getNext());
    }

    private void stopAutomatic() {
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        restartBtn.setEnabled(true);

        repeatingTask.stop();

        automated = false;
    }

    private void startAutomatic() {
        automated = true;

        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        restartBtn.setEnabled(false);

        repeatingTask.start();
    }

    private void removeAllFragments() {
        getSupportFragmentManager().beginTransaction().
                remove(currentFragment).commitAllowingStateLoss();
    }

    private void getSurveyData() {
        dialogHandler.showDialog("Initializing...");

        Observable.create(new ObservableOnSubscribe<Survey>() {
            @Override
            public void subscribe(ObservableEmitter<Survey> e) throws Exception {
                final GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Survey.class, new SurveyGsonAdapter());

                Gson gson = gsonBuilder.create();

                Survey survey =
                        gson.fromJson(
                                Utils.readFromAssetsFile(
                                        SurveyTestActivity.this, surveyFilename),
                                Survey.class
                        );
                e.onNext(survey);
                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Survey>() {
                    @Override
                    public void accept(Survey survey) throws Exception {
                        SurveyTestActivity.this.survey = survey;
                        SurveyTestActivity.this.root = survey.getQuestion();

                        SharedPreferenceUtils.putSurveyID(sharedPreferences, "21010430");

                        flowLogic = new FlowLogicImplementation(survey.getQuestion(), sharedPreferences);

                        String auth = Utils.readFromAssetsFile(SurveyTestActivity.this, "auth.json");
                        JsonParser jsonParser = new JsonParser();
                        JsonObject authJson = jsonParser.parse(auth).getAsJsonObject();

                        flowLogic.setAuthJson(authJson);
                        loadFragment(flowLogic.getNext());

                        dialogHandler.hideDialog();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        dialogHandler.hideDialog();
                    }
                });
        ;
    }

}
