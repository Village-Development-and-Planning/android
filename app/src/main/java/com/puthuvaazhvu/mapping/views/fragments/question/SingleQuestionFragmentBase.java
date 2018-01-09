package com.puthuvaazhvu.mapping.views.fragments.question;

import android.content.Context;

import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.SingleQuestionFragmentCommunication;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public abstract class SingleQuestionFragmentBase extends QuestionWithOptionUI {
    protected SingleQuestionFragmentCommunication singleQuestionFragmentCommunication;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            singleQuestionFragmentCommunication = (SingleQuestionFragmentCommunication) context;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Please implement the " + SingleQuestionFragmentCommunication.class.getSimpleName() + " on the parent ativity");
        }
    }

    public SingleQuestionFragmentCommunication getSingleQuestionFragmentCommunication() {
        return singleQuestionFragmentCommunication;
    }
}
