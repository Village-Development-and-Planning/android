package com.puthuvaazhvu.mapping.views.fragments.question;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String questionText = getQuestion().getTextForLanguage();
        String rawNumber = getQuestion().getRawNumber();

        String text = rawNumber + ". " + questionText;
        getQuestionText().setText(text);
    }

    public SingleQuestionFragmentCommunication getSingleQuestionFragmentCommunication() {
        return singleQuestionFragmentCommunication;
    }
}
