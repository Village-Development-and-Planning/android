package com.puthuvaazhvu.mapping.Survey.Fragments.DynamicTypes;

import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;

/**
 * Created by muthuveerappans on 9/21/17.
 */

public interface DynamicFragmentTypeCommunicationInterface {
    public void OnShowNextFragment(QuestionModal questionModal);

    public void OnShowPreviousFragment(QuestionModal questionModal);
}
