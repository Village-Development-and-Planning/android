package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;

/**
 * Created by muthuveerappans on 10/12/17.
 */

public abstract class SingleQuestionFragmentBase extends QuestionDataFragment {
    private TextView question_text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.single_question, container, false);
        initView(view);
        question_text = view.findViewById(R.id.question_text);

        return view;
    }

    public TextView getQuestion_text() {
        return question_text;
    }
}
