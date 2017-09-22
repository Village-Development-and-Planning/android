package com.puthuvaazhvu.mapping.Survey.Fragments.DynamicTypes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.Question.Loop.QuestionTreeRootLoopFragment;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.Question.SingleQuestion.QuestionFragment;
import com.puthuvaazhvu.mapping.R;

/**
 * Created by muthuveerappans on 9/21/17.
 */

public class NormalQuestionFragment extends BaseDynamicTypeFragment {
    TextView question_text;
    Button back_button;
    Button next_button;

    public static NormalQuestionFragment getInstance(QuestionModal questionModal) {
        NormalQuestionFragment questionFragment = new NormalQuestionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("question_data", questionModal);

        questionFragment.setArguments(bundle);

        return questionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.question_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        question_text = view.findViewById(R.id.question_text);
        back_button = view.findViewById(R.id.back_button);
        next_button = view.findViewById(R.id.next_button);

        next_button.setOnClickListener(this);
        back_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                getDynamicFragmentTypeCommunicationInterface().OnShowPreviousFragment();
                break;
            case R.id.next_button:
                getDynamicFragmentTypeCommunicationInterface().OnShowNextFragment();
                break;
        }
    }
}
