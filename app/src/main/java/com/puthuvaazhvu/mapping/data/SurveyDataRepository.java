package com.puthuvaazhvu.mapping.data;

import com.puthuvaazhvu.mapping.modals.Survey;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/28/17.
 */

// Concrete implementation of a survey data repository which loads data from the survey data source.
public class SurveyDataRepository implements DataRepository<Survey> {
    private DataSource<Survey> surveyDataSource;
    private Survey survey;

    public SurveyDataRepository(DataSource<Survey> surveyDataSource) {
        this.surveyDataSource = surveyDataSource;
    }

    @Override
    public void getAllData(final DataLoadedCallback<ArrayList<Survey>> callback) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void getData(String selection, final DataLoadedCallback<Survey> callback) {
        if (survey != null) {
            callback.onDataLoaded(survey);
        }

        surveyDataSource.getData(selection, new DataSource.DataSourceCallback<Survey>() {
            @Override
            public void onLoaded(Survey data) {
                survey = data;
                callback.onDataLoaded(data);
            }
        });
    }

    @Override
    public void saveData(Survey data) {
        surveyDataSource.saveData(data);
        refreshData();
    }

    @Override
    public void refreshData() {
        survey = null;
    }
}
