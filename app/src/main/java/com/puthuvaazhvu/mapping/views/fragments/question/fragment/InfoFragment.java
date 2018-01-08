package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.MultipleAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 10/10/17.
 */

public class InfoFragment extends QuestionDataFragment implements View.OnClickListener {
    private QuestionData questionData;
    private TextView question_text;

    public static InfoFragment getInstance(QuestionData questionData) {
        InfoFragment fragment = new InfoFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("questionData", questionData);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionData = getArguments().getParcelable("questionData");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_question, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        question_text = view.findViewById(R.id.question_text);
        question_text.setText(questionData.getSingleQuestion().getText());
    }

    @Override
    public void onBackButtonPressed(View view) {
        backButtonPressedInsideQuestion(questionData);
    }

    @Override
    public void onNextButtonPressed(View view) {
        sendQuestionToCaller(getUpdatedQuestion(), false);
    }

    private QuestionData getUpdatedQuestion() {
        // set the option questionData as the response questionData
        MultipleAnswerData multipleAnswer = new MultipleAnswerData(questionData.getSingleQuestion().getId()
                , questionData.getSingleQuestion().getText(), questionData.getOptionOptionData().getOptions());
        questionData.getOptionOptionData().setAnswerData(multipleAnswer);
        questionData.setResponseData(questionData.getOptionOptionData());
        return questionData;
    }
}
