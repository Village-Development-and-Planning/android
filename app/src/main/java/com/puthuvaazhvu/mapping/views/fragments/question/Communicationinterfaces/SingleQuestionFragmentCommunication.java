package com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public interface SingleQuestionFragmentCommunication {
    void onNextPressedFromSingleQuestion(Question question, ArrayList<Option> response);

    void onBackPressedFromSingleQuestion(Question question);

    void onError(String message);
}