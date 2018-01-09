package com.puthuvaazhvu.mapping.views.activities.main;

import android.support.v4.app.Fragment;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.helpers.FlowHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public interface Contract {
    interface View {
        void onSurveyLoaded(Survey survey);

        void onError(int messageID);

        void shouldShowSummary(Survey survey);

        void onSurveySaved(Survey survey);

        void openListOfSurveysActivity();

        void onSurveyEnd();

        void showLoading(int messageID);

        void showMessage(int messageID);

        void hideLoading();

        void toggleDefaultBackPressed(boolean toggle);

        void loadQuestionUI(Fragment fragment, String tag);
    }

    interface UserAction {

        Survey getSurvey();

        Question getCurrent();

        void setCurrent(Question question);

        /**
         * Get's the survey from the {@link com.puthuvaazhvu.mapping.data.SurveyDataRepository}
         */
        void loadSurvey(String surveyID);

        void showCurrent();

        /**
         * Get's the next question to be shown in the UI
         */
        void getNext();

        void getPrevious();

        /**
         * Main starting point of the questions flow for the UI
         */
        void initData(Survey survey, FlowHelper flowHelper);

        void finishCurrent(Question question);

        /**
         * Helper to update the given question with the updated answer data.
         *
         * @param currentQuestion
         */
        void updateCurrentQuestion(Question currentQuestion, ArrayList<Option> response, Runnable runnable);

        /**
         * Set the current question for the pointer to point to. This will be shown in the UI.
         *
         * @param index The index of the child question for the current pointer to point to.
         */
        void moveToQuestionAt(int index);

        void dumpSurveyToFile(boolean isSurveyOver, boolean isSnapshotIncomplete);
    }
}
