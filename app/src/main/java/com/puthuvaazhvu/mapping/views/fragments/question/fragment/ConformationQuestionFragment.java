package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;

/**
 * Created by muthuveerappans on 10/12/17.
 */

public class ConformationQuestionFragment extends SingleQuestionFragmentBase {
    private Data data;

    public static ConformationQuestionFragment getInstance(Data data) {
        ConformationQuestionFragment fragment = new ConformationQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        data = getArguments().getParcelable("data");

        String questionText = data.getQuestion().getText();
        getQuestion_text().setText(questionText);
    }

    @Override
    public void onBackButtonPressed(View view) {
        backButtonPressedInsideQuestion(data);
    }

    @Override
    public void onNextButtonPressed(View view) {
        sendQuestionToCaller(data, false, false);
    }
}
