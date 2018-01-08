package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 11/7/17.
 */

public class MessageQuestionFragment extends ConformationQuestionFragment {
    private QuestionData questionData;

    public static MessageQuestionFragment getInstance(QuestionData questionData) {
        MessageQuestionFragment fragment = new MessageQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("questionData", questionData);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questionData = getArguments().getParcelable("questionData");
    }

    @Override
    public void onBackButtonPressed(View view) {
        // put a dummy data here too.
        backButtonPressedInsideQuestion(questionData);
    }

    @Override
    public void onNextButtonPressed(View view) {
        sendQuestionToCaller(getUpdatedQuestion("DUMMY", questionData), false);
    }
}
