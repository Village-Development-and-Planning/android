package com.puthuvaazhvu.mapping.views.activities.survey_list;

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
    }

    interface UserAction {
        void fetchListOfSurveys();
    }
}
