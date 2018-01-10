package com.puthuvaazhvu.mapping.views.fragments.question;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.options.OptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.CreateOptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public abstract class QuestionWithOptionUI extends QuestionDataFragment {
    OptionsUI optionsUI;

    ViewGroup optionsContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.single_question, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        optionsContainer = view.findViewById(R.id.options_container);
        loadOptionUI(getQuestion(), getOptionsUIFactory());
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
