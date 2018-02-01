package com.puthuvaazhvu.mapping.views.fragments.question;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;

/**
 * Created by muthuveerappans on 10/10/17.
 */

public class InfoFragment extends SingleQuestionFragmentBase implements View.OnClickListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_question, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        question_text = view.findViewById(R.id.question_text);
        question_text.setText(QuestionUtils.getTextString(getQuestion()));
    }

    @Override
    public OptionsUIFactory getOptionsUIFactory() {
        return new OptionsUIFactory(getQuestion(), optionsContainer);
    }

    @Override
    public void onBackButtonPressed(View view) {
        getSingleQuestionFragmentCommunication().onBackPressedFromSingleQuestion(getQuestion());
    }

    @Override
    public void onNextButtonPressed(View view) {
        getSingleQuestionFragmentCommunication().onNextPressedFromSingleQuestion(getQuestion(), optionsUI.response());
    }
}
