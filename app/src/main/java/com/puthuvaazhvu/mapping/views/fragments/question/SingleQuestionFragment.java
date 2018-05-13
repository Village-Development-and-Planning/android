package com.puthuvaazhvu.mapping.views.fragments.question;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.options.OptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;
import com.puthuvaazhvu.mapping.views.fragments.question.types.QuestionFragmentTypes;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class SingleQuestionFragment extends QuestionFragmentWithOptions implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.single_question, container, false);
    }

    @Override
    public void onBackButtonPressed(View view) {
        callbacks.onBackPressed(QuestionFragmentTypes.SINGLE);
    }

    @Override
    public void onNextButtonPressed(View view) {
        ArrayList<Option> options = optionsUI.response();
        if (options == null || options.size() <= 0) {
            callbacks.onError(Utils.getErrorMessage(R.string.options_not_entered_err, getContext()));
        } else {
            callbacks.onNextPressed(QuestionFragmentTypes.SINGLE, options);
            optionsUI.onNextPressed();
        }
    }

}
