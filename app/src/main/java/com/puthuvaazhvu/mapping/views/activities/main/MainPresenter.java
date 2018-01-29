package com.puthuvaazhvu.mapping.views.activities.main;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.other.dumpdata.DumpData;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.info_file.AnswersInfoFile;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.fragments.question.ConformationQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.GPSQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.GridQuestionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.InfoFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.MessageQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.ShownTogetherFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.SingleQuestionFragment;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class MainPresenter implements Contract.UserAction {
    private final Contract.View activityView;
    private final SurveyDataRepository dataRepository;

    private FlowLogic flowLogic;

    private Survey survey;

    private final Handler uiHandler;

    private final SaveToFile saveToFile;
    private final GetFromFile getFromFile;

    private final AnswersInfoFile answersInfoFile;

    public MainPresenter(
            Contract.View activityView,
            SurveyDataRepository dataRepository,
            Handler uiHandler,
            SaveToFile saveToFile,
            GetFromFile getFromFile
    ) {
        this.activityView = activityView;
        this.dataRepository = dataRepository;
        this.uiHandler = uiHandler;
        this.saveToFile = saveToFile;
        this.getFromFile = getFromFile;
        this.answersInfoFile = new AnswersInfoFile(getFromFile, saveToFile);
    }

    @Override
    public Survey getSurvey() {
        return survey;
    }

    @Override
    public Question getCurrent() {
        return flowLogic.getCurrent().question;
    }

    @Override
    public void setCurrent(Question question) {
        flowLogic.setCurrent(question, FlowLogic.FlowData.FlowUIType.DEFAULT);
    }

    @Override
    public void loadSurvey(final String surveyID) {

        activityView.showLoading(R.string.loading);

        File file = DataFileHelpers.getSurveyDataFile(surveyID, true);

        if (file != null && file.exists()) {
            dataRepository.getSurveyFromFile(file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Survey>() {
                        @Override
                        public void accept(@NonNull Survey survey) throws Exception {
                            activityView.hideLoading();
                            activityView.onSurveyLoaded(survey);
                        }
                    });
        } else {
            activityView.hideLoading();
            activityView.onError(R.string.file_not_exist);
        }
    }

    @Override
    public void initData(Survey survey, FlowLogic flowLogic) {
        // init
        this.survey = survey;
        this.flowLogic = flowLogic;
    }

    @Override
    public void finishCurrent(Question question) {
        if (flowLogic == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        flowLogic.finishCurrent();
    }

    @Override
    public void moveToQuestionAt(int index) {
        if (flowLogic == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        FlowLogic.FlowData current = flowLogic.moveToIndexInChild(index).getCurrent();

        // call UI with the set question
        showUI(current);
    }

    @Override
    public void dumpSurveyToFile(final boolean isSurveyDone, boolean isSnapshotIncomplete) {

        activityView.showLoading(R.string.survey_file_saving_msg);

        DumpData.getInstance().dumpSurvey(
                survey,
                "" + System.currentTimeMillis(),
                getPathOfCurrentQuestion(),
                isSnapshotIncomplete,
                isSurveyDone
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Optional>() {
                               @Override
                               public void accept(@NonNull Optional optional) throws Exception {
                                   activityView.onSurveySaved(survey);
                                   activityView.hideLoading();
                                   activityView.showMessage(R.string.save_successful);

                                   if (isSurveyDone) {
                                       activityView.openListOfSurveysActivity();
                                   }
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                String errorMessage = "Error while saving file: " + throwable.getMessage();
                                Timber.e(errorMessage);
                                activityView.hideLoading();
                                activityView.onError(R.string.error_saving_survey);

                                // send report to fabric.io
                                Crashlytics.logException(new Exception(errorMessage));
                            }
                        }
                );
    }

    @Override
    public void updateCurrentQuestion(final Question question, final ArrayList<Option> response, final Runnable runnable) {
        if (flowLogic == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        // this seems to be a little heavy process, so run in background thread.
        // references does'nt matter as the process is not too big to leak memory.
        new Thread(new Runnable() {
            @Override
            public void run() {
                flowLogic.update(response);
                uiHandler.post(runnable);
            }
        }).start();
    }

    @Override
    public void showCurrent() {
        showUI(flowLogic.getCurrent());
    }

    @Override
    public void getNext() {
        if (flowLogic == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        showUI(flowLogic.getNext());
    }

    @Override
    public void getPrevious() {
        if (flowLogic == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }
        showUI(flowLogic.getPrevious());
    }

    private void showUI(FlowLogic.FlowData flowData) {
        Fragment fragment = null;

        if (flowData.flowType == FlowLogic.FlowData.FlowUIType.GRID) {
            fragment = new GridQuestionsFragment();
            activityView.loadQuestionUI(fragment, "grid");
        } else if (flowData.flowType == FlowLogic.FlowData.FlowUIType.TOGETHER) {
            fragment = new ShownTogetherFragment();
            activityView.loadQuestionUI(fragment, "shown_together");
        } else if (flowData.flowType == FlowLogic.FlowData.FlowUIType.END) {
            activityView.onSurveyEnd();
        } else {
            showSingleQuestionUI(flowData.question);
        }
    }

    private void showSingleQuestionUI(Question question) {
        Fragment fragment = null;
        String tag = "";
        // check and show UI accordingly
        switch (question.getFlowPattern().getQuestionFlow().getUiMode()) {
            case INFO:
                fragment = new InfoFragment();
                tag = "info";
                break;
            case CONFIRMATION:
                fragment = new ConformationQuestionFragment();
                tag = "confirmation";
                break;
            case MESSAGE:
                fragment = new MessageQuestionFragment();
                tag = "message";
                break;
            case SINGLE_CHOICE:
            case MULTIPLE_CHOICE:
            case INPUT:
                fragment = new SingleQuestionFragment();
                tag = "single";
                break;
            case GPS:
                fragment = new GPSQuestionFragment();
                tag = "gps";
                break;
        }

        if (fragment != null)
            activityView.loadQuestionUI(fragment, tag);
    }

    private String getPathOfCurrentQuestion() {
        Question question = flowLogic.getCurrent().question;
        if (question == null) {
            return null;
        }
        ArrayList<Integer> indexes = QuestionUtils.getPathOfQuestion(question);
        return TextUtils.join(",", indexes);
    }

}
