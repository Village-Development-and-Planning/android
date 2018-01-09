package com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces;

import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/10/18.
 */

public interface ConfirmationQuestionCommunication {
    void onBackPressedFromConformationQuestion(Question question, ArrayList<Option> response);
}
