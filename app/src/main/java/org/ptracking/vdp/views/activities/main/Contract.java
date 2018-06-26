package org.ptracking.vdp.views.activities.main;

import android.support.v4.app.Fragment;

import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.modals.Survey;
import org.ptracking.vdp.views.flow_logic.FlowLogic;

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

        void startHomeActivity();

        void updateCurrentQuestion(Question question);
    }

    interface UserAction {
        Observable<FlowLogic> init();

        FlowLogic getFlowLogic();

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
