package com.puthuvaazhvu.mapping.views.activities;

import android.support.annotation.VisibleForTesting;

import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.AnswerData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridQuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.helpers.flow.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.flow.FlowHelperBase;
import com.puthuvaazhvu.mapping.views.helpers.flow.SurveyFlowHelper;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class Presenter implements Contract.UserAction {
    private final Contract.View activityView;
    private final DataRepository<Survey> dataRepository;

    private FlowHelperBase surveyQuestionFlow;

    private Survey survey;

    public Presenter(Contract.View view, DataRepository<Survey> dataRepository) {
        this.activityView = view;
        this.dataRepository = dataRepository;
    }

    @Override
    public void getSurvey() {
        dataRepository.getData(null // Todo: add some identifier for the survey
                , new DataRepository.DataLoadedCallback<Survey>() {
                    @Override
                    public void onDataLoaded(Survey data) {
                        activityView.onSurveyLoaded(data);
                    }
                });
    }

    @Override
    public void startSurvey(Survey survey, FlowHelperBase surveyQuestionFlow) {
        // init
        setSurveyQuestionFlow(surveyQuestionFlow);
        this.survey = survey;

        // set the first question
        getNext();
    }

    public void setSurveyQuestionFlow(FlowHelperBase surveyQuestionFlow) {
        this.surveyQuestionFlow = surveyQuestionFlow;
    }

    @Override
    public void moveToQuestionAt(int index) {
        if (surveyQuestionFlow == null) {
            throw new IllegalArgumentException("The method is called too early? Call startSurvey()/getSurvey() first.");
        }

        Question current = surveyQuestionFlow.moveToIndex(index).getCurrent();

        // call UI with the set question
        showSingleQuestionUI(current);
    }

    @Override
    public void updateCurrentQuestion(QuestionData questionData) {
        surveyQuestionFlow.update(questionData);
    }

    @Override
    public void getNext() {
        // 1. get the next question to be shown
        FlowHelperBase.FlowData flowData = surveyQuestionFlow.getNext();

        // 2. remove the answered questions from the stack.
        //    This comes 2nd because the remove question are populated in the flow helper only.
        removeQuestionsFromStack();

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
        ArrayList<Question> children = question.getLatestAnswer().getChildren();
        ArrayList<GridQuestionData> data = GridQuestionData.adapter(children);
        activityView.shouldShowGrid(question.getId(), data);
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
        ArrayList<Question> toBeRemoved = surveyQuestionFlow.clearToBeRemovedList();
        if (!toBeRemoved.isEmpty())
            activityView.remove(toBeRemoved);
    }
}
