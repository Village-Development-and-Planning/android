package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 10/12/17.
 */

public class ConformationQuestionFragment extends SingleQuestionFragmentBase {
    private QuestionData questionData;

    public static ConformationQuestionFragment getInstance(QuestionData questionData) {
        ConformationQuestionFragment fragment = new ConformationQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("questionData", questionData);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        questionData = getArguments().getParcelable("questionData");

        String questionText = questionData.getSingleQuestion().getText();
        getQuestion_text().setText(questionText);
    }

    @Override
    public void onBackButtonPressed(View view) {
        backButtonPressedInsideQuestion(questionData);
    }

    @Override
    public void onNextButtonPressed(View view) {
        sendQuestionToCaller(questionData, false, false);
    }
}
