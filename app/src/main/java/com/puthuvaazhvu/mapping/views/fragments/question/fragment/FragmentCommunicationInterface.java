package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public interface FragmentCommunicationInterface {
    void onQuestionAnswered(QuestionData questionData, boolean isNewRoot, boolean shouldLogOption);

    void onBackPressedFromQuestion(QuestionData currentQuestionData);

    void onErrorWhileAnswering(String message);
}
