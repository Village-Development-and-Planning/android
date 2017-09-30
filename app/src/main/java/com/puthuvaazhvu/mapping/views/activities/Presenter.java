package com.puthuvaazhvu.mapping.views.activities;

import com.puthuvaazhvu.mapping.DataInjection;
import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Survey;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class Presenter implements Contract.UserAction {
    private final Contract.View activityView;

    public Presenter(Contract.View view) {
        this.activityView = view;
    }

    @Override
    public void getSurvey() {
        DataInjection.provideSurveyDataRepository().getData(null // Todo: add some identifier for a survey
                , new DataRepository.DataLoadedCallback<Survey>() {
                    @Override
                    public void onDataLoaded(Survey data) {
                        activityView.onSurveyLoaded(data);
                    }
                });
    }

    @Override
    public Question getNext(Question current) {
        return null;
    }
}
