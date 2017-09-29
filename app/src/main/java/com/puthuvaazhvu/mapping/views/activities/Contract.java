package com.puthuvaazhvu.mapping.views.activities;

import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;

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
         * @param question THe single question that should be shown.
         */
        void shouldShowGrid(Question question);

        /**
         * Callback called when particular single question should be shown as it is.
         *
         * @param question
         */
        void shouldShowQuestion(Question question);

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
        void getSurvey(); // Todo: Add something to uniquely identify and get the survey.

        /**
         * Gets the next question to be shown.
         * This also calls the appropriate callback to show/remove the specific fragment(s).
         *
         * @param current Current question that was answered.
         * @return Next Question from the tree.
         */
        Question getNext(Question current);
    }
}
