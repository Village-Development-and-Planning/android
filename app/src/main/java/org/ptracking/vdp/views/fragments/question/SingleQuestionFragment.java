package org.ptracking.vdp.views.fragments.question;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ptracking.vdp.R;
import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.utils.Utils;
import org.ptracking.vdp.views.fragments.question.types.QuestionFragmentTypes;

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
