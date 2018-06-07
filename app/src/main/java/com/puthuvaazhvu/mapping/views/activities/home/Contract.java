package com.puthuvaazhvu.mapping.views.activities.home;

import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.modals.surveyorinfo.SurveyorInfoFromAPI;
import com.puthuvaazhvu.mapping.views.activities.modals.CurrentSurveyInfo;

/**
 * Created by muthuveerappans on 07/06/18.
 */

public interface Contract {
    interface View {
        void onLogoutSuccessful();

        void openUploadActivity(String surveyorCode, String surveyorName);

        void onSurveyDownloadedSuccessfully(String surveyorCode, Survey survey);

        void openMainActivity(CurrentSurveyInfo surveyInfo);

        void onError(String msg);

        void showLoading();

        void hideLoading();

        void onSurveyorInfoFetched(SurveyorInfoFromAPI surveyorInfoFromAPI);
    }

    interface UserAction {
        void doLogout();

        void doUpload();

        void doDownload();

        void startSurvey();

        void getSurveyorInfo();
    }
}
