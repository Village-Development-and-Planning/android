package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.MultipleAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 10/12/17.
 */

// TODO: think for back function
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

        getBack_button().setText(getString(R.string.no));
        getNext_button().setText(getString(R.string.yes));
    }

    @Override
    public void onBackButtonPressed(View view) {
        // put a dummy data here too.
        finishCurrentQuestion(getUpdatedQuestion("NO"), true);
    }

    @Override
    public void onNextButtonPressed(View view) {
        sendQuestionToCaller(getUpdatedQuestion("YES"), false);
    }

    public QuestionData getUpdatedQuestion(String optionText) {
        InputAnswerData inputAnswerData = new InputAnswerData(questionData.getSingleQuestion().getId()
                , questionData.getSingleQuestion().getText(), optionText);

        questionData.getOptionOptionData().setAnswerData(inputAnswerData);
        questionData.setResponseData(questionData.getOptionOptionData());

        return questionData;
    }
}