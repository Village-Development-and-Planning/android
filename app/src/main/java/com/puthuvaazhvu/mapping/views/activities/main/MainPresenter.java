package com.puthuvaazhvu.mapping.views.activities.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.AuthRepository;
import com.puthuvaazhvu.mapping.data.SurveyRepository;
import com.puthuvaazhvu.mapping.filestorage.io.AnswerIO;
import com.puthuvaazhvu.mapping.filestorage.io.SnapshotIO;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListData;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogicImplementation;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic.FlowData.ErrorCodes.AUTH_ERROR;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class MainPresenter implements Contract.UserAction {
    private final Contract.View activityView;

    private FlowLogic flowLogic;

    private final Handler uiHandler;

    private final SurveyListData surveyListData;
    private Survey survey;
    private JsonObject authJson;

    private final SurveyRepository surveyRepository;
    private final AuthRepository authRepository;

    private final SharedPreferences sharedPreferences;

    MainPresenter(
            Contract.View activityView,
            Handler uiHandler,
            SurveyListData surveyListData,
            SharedPreferences sharedPreferences
    ) {
        this.activityView = activityView;
        this.uiHandler = uiHandler;

        this.surveyListData = surveyListData;

        this.surveyRepository = new SurveyRepository((Context) activityView, surveyListData.getId());
        this.authRepository = new AuthRepository((Context) activityView);

        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Observable<FlowLogic> init() {
        Observable<Survey> surveyObservable;

        if (surveyListData.isOngoing()) {
            SnapshotIO snapshotIO = new SnapshotIO(surveyListData.getSnapshotID());
            surveyObservable = snapshotIO.read();
        } else {
            surveyObservable = surveyRepository.getFromFileSystem();
        }

        Observable<JsonObject> authObservable = authRepository.getFromFileSystem();

        return Observable.zip(surveyObservable, authObservable, new BiFunction<Survey, JsonObject, FlowLogic>() {
            @Override
            public FlowLogic apply(Survey survey, JsonObject authJson) throws Exception {
                setSurvey(survey);
                setAuthJson(authJson);

                Question root = survey.getQuestion();

                FlowLogic flowLogic;

                if (surveyListData.isOngoing() && surveyListData.getSnapshotPath() != null
                        && !surveyListData.getSnapshotPath().isEmpty()) {
                    flowLogic = new FlowLogicImplementation(root, authJson, surveyListData.getSnapshotPath(), sharedPreferences);
                } else {
                    flowLogic = new FlowLogicImplementation(root, authJson, sharedPreferences);
                }

                setFlowLogic(flowLogic);

                return flowLogic;
            }
        });
    }

    @Override
    public JsonObject getAuthJson() {
        return authJson;
    }

    @Override
    public void setAuthJson(JsonObject authJson) {
        this.authJson = authJson;
    }

    @Override
    public FlowLogic getFlowLogic() {
        return flowLogic;
    }

    @Override
    public void setFlowLogic(FlowLogic flowLogic) {
        this.flowLogic = flowLogic;
    }

    @Override
    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    @Override
    public Survey getSurvey() {
        return survey;
    }

    @Override
    public void finishCurrent(Question question) {
        showUI(flowLogic.finishCurrentAndGetNext());
    }

    @Override
    public void moveToQuestionAt(int index) {
        showUI(flowLogic.moveToIndexInChild(index));
    }

    @Override
    public void dumpAnswer() {
        activityView.showLoading(R.string.loading);

        String answerID = survey.getId() + "_" + System.currentTimeMillis();
        AnswerIO answerIO = new AnswerIO(answerID);
        answerIO.save(survey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        activityView.onSurveySaved(survey);
                        activityView.hideLoading();
                        activityView.showMessage(R.string.save_successful);
                        activityView.showSurveyCompleteDialog();
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
        if (!flowLogic.update(response)) {
            activityView.onError(R.string.options_not_entered_err);
            return;
        }

        uiHandler.post(runnable);
    }

    @Override
    public void getNext() {
        showUI(flowLogic.getNext());
    }

    @Override
    public void getPrevious() {
        showUI(flowLogic.getPrevious());
    }

    private void showUI(FlowLogic.FlowData flowData) {
        if (flowData == null) {
            activityView.finishActivityWithError("Unexpected error.");
        } else if (flowData.isError()) {
            if (flowData.getErrorCode() == AUTH_ERROR) {
                activityView.finishActivityWithError("Authentication error.");
            } else {
                activityView.finishActivityWithError("Unexpected error.");
            }
        } else if (flowData.isOver()) {
            activityView.onSurveyEnd();
        } else {
            activityView.updateCurrentQuestion(flowData.getQuestion());
            activityView.loadQuestionUI(flowData.getFragment(), flowData.getQuestion().getNumber());
        }
    }

    private Question getCurrentQuestion() {
        return flowLogic.getCurrent().getQuestion();
    }

}
