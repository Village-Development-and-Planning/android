package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.MultipleAnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 10/10/17.
 */

public class InfoFragment extends QuestionFragment implements View.OnClickListener {
    private QuestionData questionData;

    private Button next_button, back_button;
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
        return inflater.inflate(R.layout.info_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        question_text = view.findViewById(R.id.question_text);
        question_text.setText(questionData.getSingleQuestion().getText());

        back_button = view.findViewById(R.id.back_button);
        next_button = view.findViewById(R.id.next_button);

        back_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                backButtonPressedInsideQuestion(questionData);
                break;
            case R.id.next_button:
                sendQuestionToCaller(getUpdatedQuestion(), false);
                break;
        }
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
