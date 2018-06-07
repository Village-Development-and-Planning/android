package com.puthuvaazhvu.mapping.views.activities.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;

/**
 * Created by muthuveerappans on 12/05/18.
 */

public class MainActivityViewModal extends ViewModel {
    private MutableLiveData<Survey> surveyMutableLiveData = new MutableLiveData<>();
    private Question currentQuestion;
    private FlowLogic flowLogic;

    public FlowLogic getFlowLogic() {
        return flowLogic;
    }

    public void setFlowLogic(FlowLogic flowLogic) {
        this.flowLogic = flowLogic;
    }

    public void setSurveyMutableLiveData(Survey survey) {
        this.surveyMutableLiveData.setValue(survey);
    }

    public LiveData<Survey> getSurvey() {
        return surveyMutableLiveData;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
