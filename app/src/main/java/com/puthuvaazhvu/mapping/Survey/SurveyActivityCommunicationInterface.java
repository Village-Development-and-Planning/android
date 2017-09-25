package com.puthuvaazhvu.mapping.Survey;

import com.puthuvaazhvu.mapping.Modals.Survey;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public interface SurveyActivityCommunicationInterface {
    public void parsedSurveyData(Survey survey);

    public void loadQuestionFragment(QuestionModal questionModal);

    public void loadLoopQuestionFragment(QuestionModal questionModal);

    public void onSurveyDone();

    public void onError(int code);
}
