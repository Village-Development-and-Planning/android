package com.puthuvaazhvu.mapping.views.activities.main;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.modals.flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.flow.PreFlow;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.utils.AuthJsonUtils;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.SharedPreferenceUtils;
import com.puthuvaazhvu.mapping.utils.saving.AnswerIOUtils;
import com.puthuvaazhvu.mapping.utils.saving.modals.AnswersInfo;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.fragments.question.ConformationQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.GPSQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.GridQuestionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.InfoFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.MessageQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.ShownTogetherFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.SingleQuestionFragment;

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

    private AnswerIOUtils saveAnswerUtils;

    public MainPresenter(
            Contract.View activityView,
            Handler uiHandler,
            SharedPreferences sharedPreferences
    ) {
        this.activityView = activityView;
        this.uiHandler = uiHandler;

        this.sharedPreferences = sharedPreferences;

        this.saveAnswerUtils = AnswerIOUtils.getInstance();
    }

    @Override
    public Survey getSurvey() {
        return survey;
    }

    @Override
    public Question getCurrent() {
        return flowLogic.getCurrent();
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

        showUI(flowLogic.finishCurrent());
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
    public void dumpSurveyToFile(final boolean isSurveyDone) {

        activityView.showLoading(R.string.survey_file_saving_msg);
        saveAnswerUtils.saveAnswer(survey, flowLogic.getCurrent(), isSurveyDone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AnswersInfo>() {
                    @Override
                    public void accept(AnswersInfo answersInfo) throws Exception {
                        activityView.onSurveySaved(survey);
                        activityView.hideLoading();
                        activityView.showMessage(R.string.save_successful);

                        if (isSurveyDone) {
                            activityView.openListOfSurveysActivity();
                        }
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
        activityView.loadQuestionUI(flowData.getFragment(), flowData.getQuestion().getRawNumber());
    }

}
