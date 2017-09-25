package com.puthuvaazhvu.mapping.Question.QuestionTree;

import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public interface QuestionTreeFragmentCommunicationInterface {
    public void onFinished(QuestionModal modifiedQuestionModal);

    void onChildFragmentPop();
}
