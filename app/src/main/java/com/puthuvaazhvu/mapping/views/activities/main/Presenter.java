package com.puthuvaazhvu.mapping.views.activities.main;

import android.os.Handler;
import android.os.Looper;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.DataFileCreator;
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

import timber.log.Timber;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class Presenter implements Contract.UserAction {
    private final Contract.View activityView;
    private final DataRepository<Survey> dataRepository;

    private FlowHelper flowHelper;

    private Survey survey;

    private final Handler uiHandler;

    private final SaveToFile saveToFile;

    public Presenter(Contract.View view, DataRepository<Survey> dataRepository, SaveToFile saveToFile, Handler uiHandler) {
        this.activityView = view;
        this.dataRepository = dataRepository;
        this.saveToFile = saveToFile;

        this.uiHandler = uiHandler;
    }

    @Override
    public void loadSurvey() {
        dataRepository.getData(null // Todo: add some identifier for the survey
                , new DataRepository.DataLoadedCallback<Survey>() {
                    @Override
                    public void onDataLoaded(Survey data) {
                        activityView.onSurveyLoaded(data);
                    }
                });
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
    public void dumpSurveyToFile() {

        activityView.showLoading(R.string.survey_file_saving_msg);

        JsonObject resultSurveyJson = survey.getAsJson().getAsJsonObject();
        Timber.i("Survey dump: \n" + resultSurveyJson.toString());

        File fileToSave = DataFileCreator.getFileToDumpSurvey(survey.getId(), false);

        if (fileToSave != null)
            dumpToFile(resultSurveyJson.toString(), fileToSave);
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

        // only if grid for children, show grid. else show normal question view
        if (flowData.flowType == FlowType.GRID) {
            showGridUI(question);
        } else if (flowData.flowType == FlowType.SINGLE) {
            showSingleQuestionUI(question);
        } else if (flowData.flowType == FlowType.END) {
            activityView.onSurveyEnd();
        }
    }

    @Override
    public void getPrevious() {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        IBackFlow.BackFlowData backFlowData = flowHelper.getPrevious();

        Question previous = backFlowData.question;

        if (previous == null) {
            activityView.onError(R.string.cannot_go_back);
        } else {
            showSingleQuestionUI(previous);
        }
    }

    private void showGridUI(Question question) {
        // get the children of the latest answer
        ArrayList<Question> children = question.getCurrentAnswer().getChildren();
        ArrayList<GridQuestionData> data = GridQuestionData.adapter(children);
        activityView.shouldShowGrid(QuestionData.adapter(question), data);
    }

    private void showSingleQuestionUI(Question question) {

        QuestionData questionData = QuestionData.adapter(question);

        // check and show UI accordingly
        if (question.getFlowPattern().getQuestionFlow().getUiMode() == QuestionFlow.UI.INFO)
            activityView.shouldShowQuestionAsInfo(questionData);
        else if (question.getFlowPattern().getQuestionFlow().getUiMode() == QuestionFlow.UI.CONFIRMATION)
            activityView.shouldShowConformationQuestion(questionData);
        else
            activityView.shouldShowSingleQuestion(questionData);
    }

    private void removeQuestionsFromStack() {
        ArrayList<Question> toBeRemoved = flowHelper.emptyToBeRemovedList();
        if (!toBeRemoved.isEmpty())
            activityView.remove(toBeRemoved);
    }

    private void dumpToFile(String toSave, File file) {
        saveToFile.execute(toSave, file, new SaveToFile.SaveToFileCallbacks() {
            @Override
            public void onFileSaved() {
                Timber.i("The data is saved to the file successfully.");
                activityView.hideLoading();
                activityView.shouldShowSummary(survey);
            }

            @Override
            public void onErrorWhileSaving(String message) {
                String errorMessage = "Error while saving file: " + message;
                Timber.e(errorMessage);
                activityView.hideLoading();

                // send report to fabric.io
                Crashlytics.logException(new Exception(errorMessage));
            }
        });
    }

}
