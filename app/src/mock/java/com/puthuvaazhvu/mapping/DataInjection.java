package com.puthuvaazhvu.mapping;

import com.puthuvaazhvu.mapping.Data.DataRepository;
import com.puthuvaazhvu.mapping.Data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.Modals.Survey;

/**
 * Created by muthuveerappans on 9/28/17.
 */

public class DataInjection {
    public static DataRepository<Survey> provideSurveyDataRepository() {
        return new SurveyDataRepository(new MockSurveyDataSource());
    }
}
