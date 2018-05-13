package com.puthuvaazhvu.mapping.views.activities.main;

import android.support.v4.app.Fragment;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListData;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public interface Contract {
    interface View {
        void onError(int messageID);

        void onSurveySaved(Survey survey);

        void showSurveyCompleteDialog();

        void onSurveyEnd();

        void showLoading(int messageID);

        void showMessage(int messageID);

        void hideLoading();

        void loadQuestionUI(Fragment fragment, String tag);

        void finishActivityWithError(String error);

        void startListOfSurveysActivity();

        void updateCurrentQuestion(Question question);
    }

    interface UserAction {
        Observable<FlowLogic> init();

        FlowLogic getFlowLogic();

        JsonObject getAuthJson();

        void setAuthJson(JsonObject authJson);

        void setFlowLogic(FlowLogic flowLogic);

        void setSurvey(Survey survey);

        Survey getSurvey();

        void getNext();

        void getPrevious();

        void finishCurrent(Question question);

        void updateCurrentQuestion(ArrayList<Option> response, Runnable runnable);

        void moveToQuestionAt(int index);

        void dumpAnswer();

        void dumpSnapshot();
    }
}
