package com.puthuvaazhvu.mapping.views.activities;

import android.os.Environment;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.DataFileCreator;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridQuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.helpers.FlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.IFlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.ResponseData;

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

    private final SaveToFile saveToFile;

    public Presenter(Contract.View view, DataRepository<Survey> dataRepository) {
        this.activityView = view;
        this.dataRepository = dataRepository;
        this.saveToFile = SaveToFile.getInstance();
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
        JsonObject resultSurveyJson = survey.getAsJson().getAsJsonObject();
        Timber.i("Survey dump: \n" + resultSurveyJson.toString());

        File fileToSave = DataFileCreator.getFileToDumpSurvey(survey.getId(), false);

        dumpToFile(resultSurveyJson.toString(), fileToSave);
    }

    @Override
    public void updateCurrentQuestion(QuestionData questionData) {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }
        flowHelper.update(ResponseData.adapter(questionData));
    }

    @Override
    public void getNext() {
        if (flowHelper == null) {
            Timber.e(Constants.LOG_TAG, "The method is called too early? Call initData()/loadSurvey() first.");
            return;
        }

        // 1. get the next question to be shown
        IFlowHelper.FlowData flowData = flowHelper.getNext();

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
            }

            @Override
            public void onErrorWhileSaving(String message) {
                Timber.e("Error while saving file: " + message);
            }
        });
    }

}
