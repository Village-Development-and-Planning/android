package com.puthuvaazhvu.mapping.views.activities.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.modals.surveyorinfo.SurveyorInfoFromAPI;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.repository.SnapshotRepository;
import com.puthuvaazhvu.mapping.repository.SnapshotRepositoryData;
import com.puthuvaazhvu.mapping.repository.SurveyRepository;
import com.puthuvaazhvu.mapping.filestorage.io.AnswerIO;
import com.puthuvaazhvu.mapping.filestorage.io.SnapshotIO;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.utils.SharedPreferenceUtils;
import com.puthuvaazhvu.mapping.views.activities.modals.CurrentSurveyInfo;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogicImplementation;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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

    private final CurrentSurveyInfo currentSurveyInfo;
    private Survey survey;
    private final SurveyorInfoFromAPI surveyorInfoFromAPI;

    private final SharedPreferenceUtils sharedPreferenceUtils;
    private final SurveyRepository surveyRepository;
    private final SnapshotRepository snapshotRepository;

    private final AnswerIO answerIO;
    private final SnapshotIO snapshotIO;

    MainPresenter(
            Contract.View activityView,
            Handler uiHandler,
            CurrentSurveyInfo currentSurveyInfo
    ) {
        Context c = (Context) activityView;

        this.answerIO = new AnswerIO();
        this.snapshotIO = new SnapshotIO();

        this.activityView = activityView;
        this.uiHandler = uiHandler;

        this.currentSurveyInfo = currentSurveyInfo;

        sharedPreferenceUtils = SharedPreferenceUtils.getInstance(c);

        surveyorInfoFromAPI = sharedPreferenceUtils.getSurveyorInfo();

        surveyRepository = new SurveyRepository(c, surveyorInfoFromAPI.getCode(), Constants.PASSWORD);
        snapshotRepository = new SnapshotRepository(c, surveyorInfoFromAPI.getCode(), Constants.PASSWORD);
    }

    @Override
    public Observable<FlowLogic> init() {
        Observable<Survey> surveyObservable;

        if (currentSurveyInfo.isOngoing()) {
            surveyObservable = snapshotRepository.get(true).map(new Function<SnapshotRepositoryData, Survey>() {
                @Override
                public Survey apply(SnapshotRepositoryData snapshotRepositoryData) throws Exception {
                    return snapshotRepositoryData.getSurvey();
                }
            });
        } else {
            surveyObservable = surveyRepository.get(true);
        }

        return surveyObservable.map(new Function<Survey, FlowLogic>() {
            @Override
            public FlowLogic apply(Survey survey) throws Exception {
                Question root = survey.getQuestion();
                FlowLogic flowLogic;

                if (currentSurveyInfo.isOngoing() && currentSurveyInfo.getSnapshotPath() != null
                        && !currentSurveyInfo.getSnapshotPath().isEmpty()) {
                    flowLogic = new FlowLogicImplementation(
                            root,
                            surveyorInfoFromAPI,
                            currentSurveyInfo.getSnapshotPath()
                    );
                } else {
                    flowLogic = new FlowLogicImplementation(root, surveyorInfoFromAPI);
                }

                setSurvey(survey);
                setFlowLogic(flowLogic);

                return flowLogic;
            }
        });
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

        answerIO.save(getSurvey(), surveyorInfoFromAPI.getCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataInfo>() {
                    @Override
                    public void accept(DataInfo dataInfo) throws Exception {
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

        Observable.just(true)
                .observeOn(Schedulers.io())
                .map(new Function<Boolean, String>() {
                    @Override
                    public String apply(Boolean aBoolean) throws Exception {
                        return TextUtils.join(
                                ",",
                                QuestionUtils.getPathOfQuestion(flowLogic.getCurrent().getQuestion())
                        );
                    }
                })
                .flatMap(new Function<String, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(String s) throws Exception {
                        return snapshotIO.save(getSurvey(), surveyorInfoFromAPI.getCode(), s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataInfo>() {
                    @Override
                    public void accept(DataInfo dataInfo) throws Exception {
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
