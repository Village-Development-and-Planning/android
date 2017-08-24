package com.puthuvaazhvu.mapping.Question.QuestionTree;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.Question.SingleQuestion.QuestionFragment;
import com.puthuvaazhvu.mapping.Question.SingleQuestion.QuestionFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.DataHelper;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class QuestionTreeFragment extends Fragment implements QuestionFragmentCommunicationInterface {
    ArrayList<QuestionModal> questionModalArrayList; // Contains all the children of the tree in a single list.
    QuestionFragment questionFragment;
    QuestionModal currentQuestion;
    int currentChildIndex = -1;
    QuestionTreeFragmentCommunicationInterface communicationInterface;
    QuestionModal root;

    public static QuestionTreeFragment getInstance(QuestionModal questionModal) {
        QuestionTreeFragment questionTreeFragment = new QuestionTreeFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("question_data", questionModal);

        questionTreeFragment.setArguments(bundle);

        return questionTreeFragment;
    }

    public void setCommunicationInterface(QuestionTreeFragmentCommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty_frame_for_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        root = getArguments().getParcelable("question_data");
        questionModalArrayList = DataHelper.convertTreeToList(root);

        loadQuestionFragment(getNextQuestion());
    }

    private void loadQuestionFragment(QuestionModal questionModal) {
        currentQuestion = questionModal;
        questionFragment = null;
        questionFragment = QuestionFragment.getInstance(questionModal);
        questionFragment.setCommunicationInterface(this);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, questionFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    // TODO: If initially NO is pressed, should not go to next question.
    @Override
    public void moveToNextQuestion(QuestionModal currentQuestion, ArrayList<OptionData> optionDataList) {
        QuestionModal nextQuestion = getNextQuestion();
        DataHelper.captureLoggedOptions(root, currentQuestion, optionDataList);

        if (nextQuestion != null) {
            loadQuestionFragment(nextQuestion);
        } else {
            // All questions options are logged.
            communicationInterface.onFinished(root);
        }
    }

    @Override
    public void moveToPreviousQuestion(QuestionModal currentQuestion) {
        loadQuestionFragment(getPreviousQuestion());
    }

    private QuestionModal getPreviousQuestion() {
        currentChildIndex -= 1;

        if (currentChildIndex <= 0) {
            currentChildIndex = 0;
        }

        return questionModalArrayList.get(currentChildIndex);
    }

    private QuestionModal getNextQuestion() {
        currentChildIndex += 1;

        if (currentChildIndex >= questionModalArrayList.size()) {
            return null;
        }

        return questionModalArrayList.get(currentChildIndex);
    }
}
