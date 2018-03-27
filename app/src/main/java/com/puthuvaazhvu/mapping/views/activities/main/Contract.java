package com.puthuvaazhvu.mapping.views.activities.main;

import android.support.v4.app.Fragment;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;

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

        void finishActivityWithError(String error);

        void onAuthSuccess(JsonObject authJson);
    }

    interface UserAction {

        Survey getSurvey();

        Question getCurrent();

        void getAuth();

        /**
         * Get's the next question to be shown in the UI
         */
        void getNext();

        void getPrevious();

        /**
         * Main starting point of the questions flow for the UI
         */
        void initData(Survey survey, FlowLogic flowLogic);

        void finishCurrent(Question question);

        void updateCurrentQuestion(ArrayList<Option> response, Runnable runnable);

        /**
         * Set the current question for the pointer to point to. This will be shown in the UI.
         *
         * @param index The index of the child question for the current pointer to point to.
         */
        void moveToQuestionAt(int index);

        void dumpAnswer();

        void dumpSnapshot();
    }
}
