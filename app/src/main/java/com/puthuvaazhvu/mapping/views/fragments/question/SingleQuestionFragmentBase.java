package com.puthuvaazhvu.mapping.views.fragments.question;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.options.CreateOptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.OptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.SingleQuestionFragmentCommunication;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public abstract class SingleQuestionFragmentBase extends QuestionDataFragment {
    protected SingleQuestionFragmentCommunication singleQuestionFragmentCommunication;

    OptionsUI optionsUI;

    ViewGroup optionsContainer;

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

        optionsContainer = view.findViewById(R.id.options_container);

        OptionsUIFactory optionsUIFactory = getOptionsUIFactory();
        if (optionsUIFactory != null)
            loadOptionUI(getQuestion(), optionsUIFactory);
    }

    public SingleQuestionFragmentCommunication getSingleQuestionFragmentCommunication() {
        return singleQuestionFragmentCommunication;
    }

    public abstract OptionsUIFactory getOptionsUIFactory();

    /**
     * Helper to load the options based on the correct option type provided.
     */
    protected void loadOptionUI(Question question, OptionsUIFactory optionsUIFactory) {
        CreateOptionsUI createOptionsUI = new CreateOptionsUI(question);
        optionsUI = createOptionsUI.createOptionsUI(optionsUIFactory);
        optionsUI.attachToRoot();
    }
}
