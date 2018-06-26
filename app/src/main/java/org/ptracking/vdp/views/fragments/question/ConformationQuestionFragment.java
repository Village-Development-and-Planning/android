package org.ptracking.vdp.views.fragments.question;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ptracking.vdp.R;
import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.modals.Text;
import org.ptracking.vdp.other.Constants;
import org.ptracking.vdp.views.fragments.question.types.QuestionFragmentTypes;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/12/17.
 */

public class ConformationQuestionFragment extends QuestionFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.single_question, container, false);
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
    }

    @Override
    public void onBackButtonPressed(View view) {
        callbacks.onBackPressed(QuestionFragmentTypes.CONFORMATION, response("NO"));
    }

    @Override
    public void onNextButtonPressed(View view) {
        callbacks.onNextPressed(QuestionFragmentTypes.CONFORMATION, response("YES"));
    }

    private ArrayList<Option> response(String text) {
        ArrayList<Option> options = new ArrayList<>();
        Option option = new Option(
                "CONFIRMATION",
                new Text(text, text),
                "CONFIRMATION"
        );
        option.setValue(text);
        options.add(option);
        return options;
    }
}
