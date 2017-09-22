package com.puthuvaazhvu.mapping.Survey.Fragments.DynamicTypes;

import com.puthuvaazhvu.mapping.Question.QuestionModal;

/**
 * Created by muthuveerappans on 9/21/17.
 */

public interface DynamicFragmentTypeCommunicationInterface {
    public void OnShowNextFragment(QuestionModal questionModal);

    public void OnShowPreviousFragment(QuestionModal questionModal);
}
