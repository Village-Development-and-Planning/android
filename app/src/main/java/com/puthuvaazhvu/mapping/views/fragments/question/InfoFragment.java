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
import com.puthuvaazhvu.mapping.views.fragments.question.types.QuestionFragmentTypes;

/**
 * Created by muthuveerappans on 10/10/17.
 */

public class InfoFragment extends QuestionFragmentWithOptions implements View.OnClickListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info_question, container, false);
    }

    @Override
    public void onBackButtonPressed(View view) {
        callbacks.onBackPressed(QuestionFragmentTypes.INFO);
    }

    @Override
    public void onNextButtonPressed(View view) {
        callbacks.onNextPressed(QuestionFragmentTypes.INFO, optionsUI.response());
    }
}
