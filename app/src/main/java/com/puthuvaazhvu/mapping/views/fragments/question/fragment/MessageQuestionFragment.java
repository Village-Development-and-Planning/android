package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.option.fragments.OptionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 11/1/17.
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
        view.findViewById(R.id.options_container).setVisibility(View.GONE);

        questionData = getArguments().getParcelable("questionData");

        String questionText = questionData.getSingleQuestion().getText();
        String rawNumber = questionData.getSingleQuestion().getRawNumber();

        String text = rawNumber + ". " + questionText;
        getQuestion_text().setText(text);
    }

    @Override
    public void onBackButtonPressed(View view) {
        backButtonPressedInsideQuestion(questionData);
    }

    @Override
    public void onNextButtonPressed(View view) {
        sendQuestionToCaller(getUpdatedQuestion("DUMMY", questionData), false);
    }
}
