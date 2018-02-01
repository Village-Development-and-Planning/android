package com.puthuvaazhvu.mapping.views.activities.survey_list;

import com.puthuvaazhvu.mapping.modals.Survey;

import java.io.File;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public interface Contract {
    interface View {
        void showLoading(int msg);

        void hideLoading();

        void onError(int msg);

        void onSurveysFetched(List<SurveyListData> surveyIds);

        void onSurveyLoaded(Survey survey);
    }

    interface UserAction {
        void fetchListOfSurveys();

        void getSurveyData(SurveyListData surveyListData);
    }
}
