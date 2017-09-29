package com.puthuvaazhvu.mapping;

import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Survey;

/**
 * Created by muthuveerappans on 9/28/17.
 */

public class DataInjection {
    public static DataRepository<Survey> provideSurveyDataRepository() {
        return new SurveyDataRepository(new MockSurveyDataSource());
    }
}
