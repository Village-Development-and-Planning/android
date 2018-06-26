package org.ptracking.vdp.views.fragments.question;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ptracking.vdp.R;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.other.Constants;
import org.ptracking.vdp.views.activities.main.MainActivityViewModal;
import org.ptracking.vdp.views.fragments.question.Communicationinterfaces.QuestionFragmentCallbacks;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public abstract class QuestionFragment extends Fragment implements View.OnClickListener {
    protected Button back_button;
    protected Button next_button;
    protected TextView questionText;

    protected QuestionFragmentCallbacks callbacks;

    protected Question currentQuestion;

    private MainActivityViewModal viewModal;

    public abstract void onBackButtonPressed(View view);

    public abstract void onNextButtonPressed(View view);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        viewModal = ViewModelProviders.of(getActivity()).get(MainActivityViewModal.class);
        callbacks = (QuestionFragmentCallbacks) context;
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        back_button = view.findViewById(R.id.back_button);
        next_button = view.findViewById(R.id.next_button);

        back_button.setOnClickListener(this);
        next_button.setOnClickListener(this);

        if (Constants.APP_LANGUAGE == Constants.Language.ENGLISH) {
            back_button.setText(getString(R.string.back));
            next_button.setText(getString(R.string.next));
        } else {
            back_button.setText(getString(R.string.back_ta));
            next_button.setText(getString(R.string.next_ta));
        }

        questionText = view.findViewById(R.id.question_text);

        currentQuestion = viewModal.getCurrentQuestion();

        setQuestionText(currentQuestion);
        toggleBackButtonBasedOnQuestion(currentQuestion);
    }

    protected Button getBackButton() {
        return back_button;
    }

    protected Button getNextButton() {
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

    private void toggleBackButtonBasedOnQuestion(Question question) {
        if (question.getFlowPattern().getQuestionFlow().isBack()) {
            getBackButton().setVisibility(View.VISIBLE);
        } else {
            getBackButton().setVisibility(View.INVISIBLE);
        }
    }

    private void setQuestionText(Question question) {
        String questionTextString = question.getTextString();
        String rawNumber = question.getNumber();

        String text = rawNumber + ". " + questionTextString;

        this.questionText.setText(text);
    }
}
