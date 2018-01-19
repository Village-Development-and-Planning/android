package com.puthuvaazhvu.mapping.views.fragments.question;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.views.fragments.options.OptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.CreateOptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.ConfirmationQuestionCommunication;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/12/17.
 */

public class ConformationQuestionFragment extends SingleQuestionFragmentBase {
    protected ConfirmationQuestionCommunication conformationQuestionFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            conformationQuestionFragment = (ConfirmationQuestionCommunication) context;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Please implement the " + ConfirmationQuestionCommunication.class.getSimpleName() + " on the parent ativity");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Constants.APP_LANGUAGE == Constants.Language.ENGLISH) {
            getBackButton().setText(getText(R.string.no));
            getNextButton().setText(getText(R.string.yes));
        } else {
            getBackButton().setText(getText(R.string.no_ta));
            getNextButton().setText(getText(R.string.yes_ta));
        }

        String questionText = getQuestion().getTextForLanguage();
        getQuestionText().setText(questionText);
    }

    @Override
    public OptionsUIFactory getOptionsUIFactory() {
        return new OptionsUIFactory(getQuestion(), optionsContainer);
    }

    @Override
    public void onBackButtonPressed(View view) {
        conformationQuestionFragment.onBackPressedFromConformationQuestion(getQuestion(), response("NO"));
    }

    @Override
    public void onNextButtonPressed(View view) {
        getSingleQuestionFragmentCommunication().onNextPressedFromSingleQuestion(getQuestion(), response("YES"));
    }

    private ArrayList<Option> response(String text) {
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option(
                "",
                "CONFIRMATION",
                new Text("", text, text, ""),
                "",
                ""
        ));
        return options;
    }
}
