package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 1/8/18.
 */

public abstract class QuestionDataFragment extends QuestionFragment {
    QuestionData questionData;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questionData = getArguments().getParcelable("questionData");

        if (isBackButtonEnabled()) {
            getBack_button().setVisibility(View.VISIBLE);
        } else {
            getBack_button().setVisibility(View.INVISIBLE);
        }
    }

    public QuestionData getQuestionData() {
        return questionData;
    }

    public boolean isBackButtonEnabled() {
        return questionData.getSingleQuestion().isBack();
    }
}
