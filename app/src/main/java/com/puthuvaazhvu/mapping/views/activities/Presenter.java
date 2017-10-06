package com.puthuvaazhvu.mapping.views.activities;

import android.support.annotation.VisibleForTesting;
import android.support.v4.app.FragmentManager;

import com.puthuvaazhvu.mapping.DataInjection;
import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.Flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.Flow.ExitFlow;
import com.puthuvaazhvu.mapping.modals.Flow.FlowPattern;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.managers.StackFragmentImpl;
import com.puthuvaazhvu.mapping.views.managers.operation.CascadeOperation;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class Presenter implements Contract.UserAction {
    private final Contract.View activityView;
    private final DataRepository<Survey> dataRepository;

    private Survey survey;
    private Question currentQuestion;
    private int currentRootQuestionIndex = 0;
    private ArrayList<Question> questionsToBeRemoved = new ArrayList<>();

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
                        Presenter.this.survey = data;
                        activityView.onSurveyLoaded(data);
                    }
                });
    }

    @Override
    public void getNext() {
        // remove the answered questions from the stack
        removeQuestionsFromStack();

        currentQuestion = getNextQuestion();
        // Todo: show the UI.
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public int getCurrentRootQuestionIndex() {
        return currentRootQuestionIndex;
    }

    private void removeQuestionsFromStack() {
        activityView.remove(questionsToBeRemoved);

        // clear the remove reference list as we can add new set of questions later and
        // we don't remove the old questions again.
        questionsToBeRemoved.clear();
    }

    private Question getNextQuestion() {
        if (currentQuestion == null) {
            // start of the survey
            currentQuestion = survey.getQuestionList().get(currentRootQuestionIndex);
            currentRootQuestionIndex += 1;
        } else if (currentQuestion.isRoot() && currentQuestion.isLeaf()) {
            // one among the survey children question
            currentQuestion = survey.getQuestionList().get(currentRootQuestionIndex);
            currentRootQuestionIndex += 1;
        } else {
            // the current question has children
            ChildFlow childFlow = currentQuestion.getFlowPattern().getChildFlow();
            ChildFlow.Modes childFlowMode = childFlow.getMode();
            switch (childFlowMode) {
                case CASCADE:
                    ArrayList<Question> children = currentQuestion.getChildren();
                    Question nextQuestion = null;
                    for (Question q : children) {
                        if (q.isAnswered()) {
                            questionsToBeRemoved.add(q);
                            continue;
                        }
                        nextQuestion = q;
                        break;
                    }
                    if (nextQuestion == null) {
                        currentQuestion = currentQuestion.getParent();
                        currentQuestion = getNextQuestion();
                    } else {
                        currentQuestion = nextQuestion;
                    }
                    break;
            }
        }
        return currentQuestion;
    }

    @Override
    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    @Override
    public void setCurrentQuestionRoot(Question questionRoot, int index) {
        currentRootQuestionIndex = index;
        if (currentRootQuestionIndex >= survey.getQuestionList().size()) {
            // all questions have been answered. So throw an exception for debugging
            throw new IllegalArgumentException("No more questions in the survey.");
        } else {
            this.currentQuestion = survey.getQuestionList().get(currentRootQuestionIndex);
        }
    }
}
