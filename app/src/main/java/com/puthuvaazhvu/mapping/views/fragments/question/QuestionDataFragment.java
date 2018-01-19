package com.puthuvaazhvu.mapping.views.fragments.question;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.flow_logic.FlowLogic;
import com.puthuvaazhvu.mapping.views.fragments.question.Communicationinterfaces.QuestionDataFragmentCommunication;

/**
 * Created by muthuveerappans on 10/12/17.
 */

public abstract class QuestionDataFragment extends QuestionFragment {
    protected QuestionDataFragmentCommunication questionDataFragmentCommunication;
    TextView question_text;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            questionDataFragmentCommunication = (QuestionDataFragmentCommunication) context;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Please implement the " + QuestionDataFragmentCommunication.class.getSimpleName() + " on the parent ativity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.single_question, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        question_text = view.findViewById(R.id.question_text);

        if (questionDataFragmentCommunication.getCurrentQuestionFromActivity().getFlowPattern().getQuestionFlow().isBack()) {
            getBackButton().setVisibility(View.VISIBLE);
        } else {
            getBackButton().setVisibility(View.INVISIBLE);
        }
    }

    public TextView getQuestionText() {
        return question_text;
    }

    public Question getQuestion() {
        return questionDataFragmentCommunication.getCurrentQuestionFromActivity();
    }

    public QuestionDataFragmentCommunication getQuestionDataFragmentCommunication() {
        return questionDataFragmentCommunication;
    }
}
