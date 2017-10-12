package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.Data;

/**
 * Created by muthuveerappans on 10/12/17.
 */

public abstract class SingleQuestionFragmentBase extends QuestionFragment implements View.OnClickListener {
    private TextView question_text;
    private Button back_button;
    private Button next_button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.single_question, container, false);
        question_text = view.findViewById(R.id.question_text);
        back_button = view.findViewById(R.id.back_button);
        next_button = view.findViewById(R.id.next_button);

        back_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
        return view;
    }

    @Override
    public abstract void onViewCreated(View view, @Nullable Bundle savedInstanceState);

    public abstract void onBackButtonPressed(View view);

    public abstract void onNextButtonPressed(View view);

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                onBackButtonPressed(view);
                break;
            case R.id.next_button:
                onNextButtonPressed(view);
                break;
        }
    }

    public TextView getQuestion_text() {
        return question_text;
    }

    public Button getBack_button() {
        return back_button;
    }

    public Button getNext_button() {
        return next_button;
    }
}
