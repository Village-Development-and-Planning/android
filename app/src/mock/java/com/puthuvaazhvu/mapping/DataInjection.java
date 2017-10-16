package com.puthuvaazhvu.mapping;

import android.content.Context;

import com.puthuvaazhvu.mapping.data.DataRepository;
import com.puthuvaazhvu.mapping.data.SurveyDataRepository;
import com.puthuvaazhvu.mapping.modals.Survey;

/**
 * Created by muthuveerappans on 9/28/17.
 */

public class DataInjection {
    public static DataRepository<Survey> provideSurveyDataRepository(Context context) {
        return new SurveyDataRepository(new MockSurveyDataSource(context));
    }
}
