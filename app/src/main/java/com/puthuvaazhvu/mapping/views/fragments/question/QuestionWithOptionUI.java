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
import com.puthuvaazhvu.mapping.views.fragments.options.OptionsUIFactory;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public abstract class QuestionWithOptionUI extends QuestionDataFragment {
    TextView question_text;

    OptionsUI optionsUI;

    ViewGroup optionsContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.single_question, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        question_text = view.findViewById(R.id.question_text);
        optionsContainer = view.findViewById(R.id.options_container);
        loadOptionUI(getQuestion());
    }

    public TextView getQuestionText() {
        return question_text;
    }

    /**
     * Helper to load the options based on the correct option type provided.
     */
    protected void loadOptionUI(Question question) {
        OptionsUIFactory optionsUIFactory = new OptionsUIFactory(question, optionsContainer);
        optionsUI = optionsUIFactory.createOptionsUI();
        optionsUI.attachToRoot();
    }
}
