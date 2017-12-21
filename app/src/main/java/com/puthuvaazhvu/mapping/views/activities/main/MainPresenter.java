package com.puthuvaazhvu.mapping.views.activities.main;

import android.os.Handler;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.other.dumpdata.DumpData;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.info_file.AnswersInfoFile;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridQuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.helpers.FlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.next_flow.IFlow;
import com.puthuvaazhvu.mapping.views.helpers.ResponseData;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.IBackFlow;

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

    private FlowHelper flowHelper;

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
    public Question getCurrent() {
        return flowHelper.getCurrent();
    }

    @Override
    public void setCurrent(Question question) {
        flowHelper.setCurrent(question);
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
    public void initData(Survey survey, FlowHelper flowHelper) {
        // init
        setSurveyQuestionFlow(flowHelper);
        this.survey = survey;
    }

    @Override
    public void finishCurrent(QuestionData questionData) {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        flowHelper.finishCurrentQuestion();
    }

    public void setSurveyQuestionFlow(FlowHelper flowHelper) {
        this.flowHelper = flowHelper;
    }

    @Override
    public void moveToQuestionAt(int index) {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        Question current = flowHelper.moveToIndex(index).getCurrent();

        // call UI with the set question
        showSingleQuestionUI(current);
    }

    @Override
    public void dumpSurveyToFile(final boolean isSurveyDone) {

        activityView.showLoading(R.string.survey_file_saving_msg);

        DumpData.getInstance().dumpSurvey(
                survey,
                "" + System.currentTimeMillis(),
                getPathOfCurrentQuestion(),
                false,
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
    public void updateCurrentQuestion(final QuestionData questionData, final Runnable runnable) {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        // this seems to be a little heavy process, so run in background thread.
        // references does'nt matter as the process is not too big to leak memory.
        new Thread(new Runnable() {
            @Override
            public void run() {
                flowHelper.update(ResponseData.adapter(questionData));
                uiHandler.post(runnable);
            }
        }).start();
    }

    @Override
    public void showCurrent() {
        IFlow.FlowData flowData = IFlow.FlowData.getFlowData(flowHelper.getCurrent());
        showUI(flowData);
    }

    @Override
    public void getNext() {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        // 1. get the next question to be shown
        IFlow.FlowData flowData = flowHelper.getNext();

        // 2. remove the answered questions from the stack.
        //    This comes 2nd because the remove question are populated in the flow helper only.
        // removeQuestionsFromStack();

        // 3. show the new question
        Question question = flowData.question;

        // check question UI flow
        if (question != null) {

            QuestionFlow questionFlow = question.getFlowPattern().getQuestionFlow();

            while (questionFlow.getUiMode() == QuestionFlow.UI.NONE) {

                // create dummy answer
                ArrayList<Option> dummyOptions = new ArrayList<>();
                dummyOptions.add(
                        new Option(
                                "-1",
                                "DUMMY",
                                new Text("-1", "DUMMY", "DUMMY", null),
                                "",
                                "-1"
                        )
                );

                ResponseData responseData = new ResponseData("-1", question.getRawNumber(), dummyOptions);
                flowHelper.update(responseData);

                flowData = flowHelper.getNext();
                question = flowData.question;
                questionFlow = question.getFlowPattern().getQuestionFlow();
            }
        }

        showUI(flowData);
    }

    @Override
    public void getPrevious() {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        IFlow.FlowData flowData = flowHelper.getPrevious();
        showUI(flowData);
    }

    private void showUI(IFlow.FlowData flowData) {
        Question question = flowData.question;
        // only if grid for children, show grid. else show normal question view
        if (flowData.flowType == FlowType.GRID) {
            showGridUI(question);
        } else if (flowData.flowType == FlowType.SINGLE) {
            showSingleQuestionUI(question);
        } else if (flowData.flowType == FlowType.END) {
            activityView.onSurveyEnd();
        } else if (flowData.flowType == FlowType.TOGETHER) {
            activityView.shouldShowTogetherQuestion(question, QuestionData.adapter(question));
        } else {
            if (question != null)
                Timber.e("Invalid UI data provided. number:  " + question.getRawNumber());
            activityView.onError(R.string.invalid_data);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    activityView.hideLoading();
                }
            });
        }
    }

    private String getPathOfCurrentQuestion() {
        Question question = flowHelper.getCurrent();
        if (question == null) {
            return null;
        }
        ArrayList<Integer> indexes = question.getPathOfCurrentQuestion();
        return TextUtils.join(",", indexes);
    }

    private void showGridUI(Question question) {
        // get the children of the latest answer
        ArrayList<Question> children = question.getLatestAnswer().getChildren();
        ArrayList<GridQuestionData> data = GridQuestionData.adapter(children);
        activityView.shouldShowGrid(QuestionData.adapter(question), data);
    }

    private void showSingleQuestionUI(Question question) {

        if (question.getFlowPattern() == null) {
            if (question.isRoot()) {
                activityView.toggleDefaultBackPressed(true);
            }
            return;
        }

        QuestionData questionData = QuestionData.adapter(question);

        // check and show UI accordingly
        switch (question.getFlowPattern().getQuestionFlow().getUiMode()) {
            case INFO:
                activityView.shouldShowQuestionAsInfo(questionData);
                break;
            case CONFIRMATION:
                activityView.shouldShowConformationQuestion(questionData);
                break;
            case MESSAGE:
                activityView.shouldShowMessageQuestion(questionData);
                break;
            default:
                activityView.shouldShowSingleQuestion(questionData);
        }
    }

}
