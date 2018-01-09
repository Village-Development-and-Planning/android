package com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces;

import com.puthuvaazhvu.mapping.modals.Question;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public interface GridQuestionFragmentCommunication {
    void onBackPressedFromGrid(Question question);

    void onNextPressedFromGrid(Question question);

    void onQuestionSelectedFromGrid(Question question, int pos);

}
