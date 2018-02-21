package com.puthuvaazhvu.mapping.views.activities.main;

import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.filestorage.AnswerIO;
import com.puthuvaazhvu.mapping.filestorage.SnapshotIO;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class MainPresenter implements Contract.UserAction {
    private final Contract.View activityView;

    private FlowLogic flowLogic;

    private Survey survey;

    private final Handler uiHandler;

    private final SharedPreferences sharedPreferences;

    public MainPresenter(
            Contract.View activityView,
            Handler uiHandler,
            SharedPreferences sharedPreferences
    ) {
        this.activityView = activityView;
        this.uiHandler = uiHandler;

        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Survey getSurvey() {
        return survey;
    }

    @Override
    public Question getCurrent() {
        return flowLogic.getCurrent().getQuestion();
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

        showUI(flowLogic.finishCurrentAndGetNext());
    }

    @Override
    public void moveToQuestionAt(int index) {
        if (flowLogic == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        showUI(flowLogic.moveToIndexInChild(index));
    }

    @Override
    public void dumpAnswer() {
        activityView.showLoading(R.string.loading);

        String answerID = survey.getId() + "_" + System.currentTimeMillis();
        AnswerIO answerIO = new AnswerIO(survey.getId(), survey.getName(), answerID);
        answerIO.save(survey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        activityView.onSurveySaved(survey);
                        activityView.hideLoading();
                        activityView.showMessage(R.string.save_successful);
                        activityView.openListOfSurveysActivity();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        String errorMessage = "Error while saving file: " + throwable.getMessage();
                        Timber.e(errorMessage);
                        activityView.hideLoading();
                        activityView.onError(R.string.error_saving_survey);
                    }
                });
    }

    @Override
    public void dumpSnapshot() {
        activityView.showLoading(R.string.loading);

        String snapshotID = survey.getId() + "_" + System.currentTimeMillis();
        SnapshotIO snapshotIO
                = new SnapshotIO(
                TextUtils.join(",", QuestionUtils.getPathOfQuestion(flowLogic.getCurrent().getQuestion())),
                snapshotID,
                survey.getId(),
                survey.getName()
        );

        snapshotIO.save(survey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        activityView.onSurveySaved(survey);
                        activityView.hideLoading();
                        activityView.showMessage(R.string.save_successful);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        String errorMessage = "Error while saving file: " + throwable.getMessage();
                        Timber.e(errorMessage);
                        activityView.hideLoading();
                        activityView.onError(R.string.error_saving_survey);
                    }
                });
    }

    @Override
    public void updateCurrentQuestion(final ArrayList<Option> response, final Runnable runnable) {
        if (flowLogic == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        if (!flowLogic.update(response)) {
            activityView.onError(R.string.options_not_entered_err);
            return;
        }

        uiHandler.post(runnable);
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
        if (flowData == null) {
            activityView.onSurveyEnd();
            return;
        }
        activityView.loadQuestionUI(flowData.getFragment(), flowData.getQuestion().getNumber());
    }

}
