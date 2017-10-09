package com.puthuvaazhvu.mapping.views.activities;

import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.Flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridData;
import com.puthuvaazhvu.mapping.views.helpers.data.QuestionDataHelper;
import com.puthuvaazhvu.mapping.views.helpers.flow.QuestionFlowHelper;
import com.puthuvaazhvu.mapping.views.helpers.flow.QuestionFlowHelperImpl;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class Presenter implements Contract.UserAction {
    private final Contract.View activityView;
    private final DataRepository<Survey> dataRepository;
    private Survey survey;
    private Question currentQuestion;
    private QuestionFlowHelper questionFlowHelper;
    private QuestionDataHelper questionDataHelper;

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
    public void startSurvey(Survey survey) {
        // always the survey has only one root question
        Question root = survey.getQuestionList().get(0);

        // init
        QuestionFlowHelper questionFlowHelper = new QuestionFlowHelperImpl(root);
        QuestionDataHelper questionDataHelper = new QuestionDataHelper(root);

        setData(survey, questionFlowHelper, questionDataHelper);

        // set the first question
        getNext();
    }

    public void setData(Survey data, QuestionFlowHelper questionFlowHelper, QuestionDataHelper questionDataHelper) {
        this.survey = data;
        this.questionFlowHelper = questionFlowHelper;
        this.questionDataHelper = questionDataHelper;
    }

    @Override
    public void setCurrentQuestion(Data currentQuestion) {
        if (questionDataHelper == null) {
            throw new IllegalArgumentException("The method is called too early? Call getSurvey() first.");
        }

        // search for the ID in the current root index only
        this.currentQuestion = questionDataHelper.find(currentQuestion.getQuestion().getId());

        // call UI with the set question
        sendDataToCaller(this.currentQuestion);
    }

    @Override
    public void updateCurrentQuestion(Data data) {
        if (data.getQuestion().getId().equals(currentQuestion.getId())) {
            currentQuestion = QuestionDataHelper.OtherHelpers.updateQuestion(data, currentQuestion);
        } else {
            throw new IllegalArgumentException("The current question and the answered question are not the same.\n" +
                    "Current Question ID: " + currentQuestion.getId() + "\n" +
                    "Answered Question ID: " + data.getQuestion().getId());
        }
    }

    @Override
    public void getNext() {
        // 1. remove the answered questions from the stack
        removeQuestionsFromStack();

        // 2. get the next question to be shown
        currentQuestion = questionFlowHelper.getNext();

        // 3. show the new question
        sendDataToCaller(currentQuestion);
    }

    private void sendDataToCaller(Question currentQuestion) {
        // only if grid for children, show grid. else show normal question view
        if (currentQuestion.getFlowPattern().getChildFlow().getUiToBeShown() == ChildFlow.UI.GRID) {
            ArrayList<GridData> data = QuestionDataHelper.Adapters.getDataForGrid(currentQuestion);
            activityView.shouldShowGrid(currentQuestion.getId(), data);
        } else {
            Data data = Data.adapter(currentQuestion);
            activityView.shouldShowSingleQuestion(data);
        }
    }

    private void removeQuestionsFromStack() {
        activityView.remove(questionFlowHelper.clearToBeRemovedList());
    }
}
