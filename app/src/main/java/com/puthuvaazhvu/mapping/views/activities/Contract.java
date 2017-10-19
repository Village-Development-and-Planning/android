package com.puthuvaazhvu.mapping.views.activities;

import android.support.annotation.VisibleForTesting;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.GridQuestionData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;
import com.puthuvaazhvu.mapping.views.helpers.FlowHelper;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public interface Contract {
    interface View {
        void onSurveyLoaded(Survey survey);

        void onError(String message);

        /**
         * Callback called when the children questions need to be shown in a grid
         *
         * @param question The single question that should be shown.
         * @param parent   parent question
         */
        void shouldShowGrid(QuestionData parent, ArrayList<GridQuestionData> question);

        /**
         * Callback called when particular single question should be shown as it is.
         *
         * @param question
         */
        void shouldShowSingleQuestion(QuestionData question);

        /**
         * Callback called when the type of the {@link Question} is INFO.
         *
         * @param question
         */
        void shouldShowQuestionAsInfo(QuestionData question);

        /**
         * Callback called when the type of the {@link Question} is CONFORMATION.
         *
         * @param question
         */
        void shouldShowConformationQuestion(QuestionData question);

        void onSurveyEnd();

        /**
         * Callback called when a particular question should be removed from the stack.
         *
         * @param question The question to be removed.
         */
        void remove(Question question);

        /**
         * Callback called when a list of questions needs to be removed from the stack.
         *
         * @param questions The list of removal questions.
         */
        void remove(ArrayList<Question> questions);
    }

    interface UserAction {
        /**
         * Get's the survey from the {@link com.puthuvaazhvu.mapping.data.DataRepository}
         */
        void loadSurvey(); // Todo: Add something to uniquely identify and get the survey.

        /**
         * Get's the next question to be shown in the UI
         */
        void getNext();

        /**
         * Main starting point of the questions flow for the UI
         */
        void initData(Survey survey, FlowHelper flowHelper);

        void finishCurrent(QuestionData questionData);

        /**
         * Helper to update the given question with the updated answer data.
         *
         * @param currentQuestion
         */
        void updateCurrentQuestion(QuestionData currentQuestion);

        /**
         * Set the current question for the pointer to point to. This will be shown in the UI.
         *
         * @param index The index of the child question for the current pointer to point to.
         */
        void moveToQuestionAt(int index);

        void dumpSurveyToFile();
    }
}
