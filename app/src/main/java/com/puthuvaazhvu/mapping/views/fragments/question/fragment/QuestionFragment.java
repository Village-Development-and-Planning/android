package com.puthuvaazhvu.mapping.views.fragments.question.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public abstract class QuestionFragment extends Fragment implements View.OnClickListener {
    protected FragmentCommunicationInterface communicationInterface;

    protected Button back_button;
    protected Button next_button;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicationInterface = (FragmentCommunicationInterface) context;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Please implement the " + FragmentCommunicationInterface.class.getSimpleName() + " on the parent ativity");
        }
    }

    protected void initView(View view) {
        back_button = view.findViewById(R.id.back_button);
        next_button = view.findViewById(R.id.next_button);

        back_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Constants.APP_LANGUAGE == Constants.Language.ENGLISH) {
            back_button.setText(getString(R.string.back));
            next_button.setText(getString(R.string.next));
        } else {
            back_button.setText(getString(R.string.back_ta));
            next_button.setText(getString(R.string.next_ta));
        }
    }

    public void sendQuestionToCaller(QuestionData questionData, boolean isNewRoot) {
        communicationInterface.onQuestionAnswered(questionData, isNewRoot);
    }

    public void finishCurrentQuestion(QuestionData questionData, boolean shouldLogOptions) {
        communicationInterface.finishCurrentQuestion(questionData, shouldLogOptions);
    }

    public void backButtonPressedInsideQuestion(QuestionData questionData) {
        communicationInterface.onBackPressedFromQuestion(questionData);
    }

    public void onError(String message) {
        communicationInterface.onErrorWhileAnswering(message);
    }

    public abstract void onBackButtonPressed(View view);

    public abstract void onNextButtonPressed(View view);

    public Button getBack_button() {
        return back_button;
    }

    public Button getNext_button() {
        return next_button;
    }

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
}
