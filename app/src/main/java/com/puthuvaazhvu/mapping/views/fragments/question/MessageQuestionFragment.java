package com.puthuvaazhvu.mapping.views.fragments.question;

import android.view.View;

import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;

/**
 * Created by muthuveerappans on 11/7/17.
 */

public class MessageQuestionFragment extends SingleQuestionFragmentBase {
    @Override
    public void onBackButtonPressed(View view) {
        getSingleQuestionFragmentCommunication().onBackPressedFromSingleQuestion(getQuestion());
    }

    @Override
    public void onNextButtonPressed(View view) {
        getSingleQuestionFragmentCommunication().onNextPressedFromSingleQuestion(getQuestion(), optionsUI.response());
    }

    @Override
    public OptionsUIFactory getOptionsUIFactory() {
        return new OptionsUIFactory(getQuestion(), optionsContainer);
    }
}
