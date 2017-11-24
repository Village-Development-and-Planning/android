package com.puthuvaazhvu.mapping.views.activities.survey_list;

import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswerDataModal;

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

        void onSurveyLoaded(Survey survey, SurveyListData.SurveySnapShot snapshot);
    }

    interface UserAction {
        void fetchListOfSurveys();

        void getSurveyFromFile(File file, SurveyListData.SurveySnapShot snapshot);
    }
}
