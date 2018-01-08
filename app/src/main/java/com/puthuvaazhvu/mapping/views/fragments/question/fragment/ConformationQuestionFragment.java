package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.InputAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.MultipleAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.SingleAnswerData;
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
        super.onViewCreated(view, savedInstanceState);

        questionData = getArguments().getParcelable("questionData");

        String questionText = questionData.getSingleQuestion().getText();
        getQuestion_text().setText(questionText);
    }

    @Override
    public void onBackButtonPressed(View view) {
        // put a dummy data here too.
        finishCurrentQuestion(getUpdatedQuestion("NO", questionData), true);
    }

    @Override
    public void onNextButtonPressed(View view) {
        sendQuestionToCaller(getUpdatedQuestion("YES", questionData), false);
    }

    public static QuestionData getUpdatedQuestion(String optionText, QuestionData questionData) {
        InputAnswerData inputAnswerData = new InputAnswerData(questionData.getSingleQuestion().getId()
                , questionData.getSingleQuestion().getText(), optionText);

        questionData.getOptionOptionData().setAnswerData(inputAnswerData);
        questionData.setResponseData(questionData.getOptionOptionData());

        return questionData;
    }
}
