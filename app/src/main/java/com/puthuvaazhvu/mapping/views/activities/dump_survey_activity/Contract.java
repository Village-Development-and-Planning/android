package com.puthuvaazhvu.mapping.views.activities.dump_survey_activity;

import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public interface Contract {
    interface View {
        void onError(int msgID);

        void showLoading(int msgID);

        void hideLoading();

        void onSurveyInfoFetched(List<SurveyInfoData> surveyInfoList);

        void finishActivity();
    }

    interface UserAction {
        void fetchListOfSurveys();

        void save(List<SurveyInfoData> surveyInfoData);
    }
}
