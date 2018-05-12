package com.puthuvaazhvu.mapping.views.activities.survey_list;

import android.arch.lifecycle.ViewModel;

/**
 * Created by muthuveerappans on 12/05/18.
 */

public class SurveyListActivityViewModal extends ViewModel {
    private SurveyListData currentSelectedSurvey;

    public SurveyListData getCurrentSelectedSurvey() {
        return currentSelectedSurvey;
    }

    public void setCurrentSelectedSurvey(SurveyListData currentSelectedSurvey) {
        this.currentSelectedSurvey = currentSelectedSurvey;
    }
}
