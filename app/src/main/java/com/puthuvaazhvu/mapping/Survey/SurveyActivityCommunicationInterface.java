package com.puthuvaazhvu.mapping.Survey;

import com.puthuvaazhvu.mapping.Modals.Survey;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public interface SurveyActivityCommunicationInterface {
    public void parsedSurveyData(Survey survey);

    public void onError(int code);
}
