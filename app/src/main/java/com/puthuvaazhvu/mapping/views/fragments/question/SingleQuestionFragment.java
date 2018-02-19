package com.puthuvaazhvu.mapping.views.fragments.question;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class SingleQuestionFragment extends SingleQuestionFragmentBase implements View.OnClickListener {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String questionText = getQuestion().getTextString();
        String rawNumber = getQuestion().getNumber();

        String text = rawNumber + ". " + questionText;
        getQuestionText().setText(text);
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
        ArrayList<Option> options = optionsUI.response();
        if (options == null || options.size() <= 0) {
            getSingleQuestionFragmentCommunication().onError(Utils.getErrorMessage(R.string.options_not_entered_err, getContext()));
        } else {
            getSingleQuestionFragmentCommunication().onNextPressedFromSingleQuestion(getQuestion(), options);
            optionsUI.onNextPressed();
        }
    }

}
