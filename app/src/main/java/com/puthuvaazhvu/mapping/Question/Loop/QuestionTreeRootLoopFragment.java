package com.puthuvaazhvu.mapping.Question.Loop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.Grid.QuestionTreeRootAsGridFragment;
import com.puthuvaazhvu.mapping.Question.Grid.QuestionTreeRootAsGridFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.Question.SingleQuestion.QuestionFragment;
import com.puthuvaazhvu.mapping.Question.SingleQuestion.QuestionFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.R;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public class QuestionTreeRootLoopFragment extends Fragment
        implements QuestionFragmentCommunicationInterface, QuestionTreeRootAsGridFragmentCommunicationInterface {
    QuestionModal questionModal;
    QuestionFragment questionFragment;
    QuestionTreeRootAsGridFragment questionTreeRootAsGridFragment;
    QuestionTreeRootLoopFragmentPresenter presenter;

    public static QuestionTreeRootLoopFragment getInstance(QuestionModal questionModal) {
        QuestionTreeRootLoopFragment questionTreeRootLoopFragment = new QuestionTreeRootLoopFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("question_data", questionModal);

        questionTreeRootLoopFragment.setArguments(bundle);

        return questionTreeRootLoopFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty_frame_for_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        presenter = new QuestionTreeRootLoopFragmentPresenter();
        questionModal = getArguments().getParcelable("question_data");
        loadRootQuestionFragment(questionModal);
    }

    private void loadRootQuestionFragment(QuestionModal questionModal) {
        questionFragment = null;
        questionFragment = QuestionFragment.getInstance(questionModal);
        questionFragment.setCommunicationInterface(this);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, questionFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void loadRootQuestionGrid(QuestionModal questionModal) {
        questionTreeRootAsGridFragment = null;
        questionTreeRootAsGridFragment = QuestionTreeRootAsGridFragment.getInstance(questionModal);
        questionTreeRootAsGridFragment.setCommunicationInterface(this);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, questionTreeRootAsGridFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void moveToNextQuestion(QuestionModal currentQuestion, ArrayList<OptionData> optionDataList) {
        presenter.insertOptionDataToMap(optionDataList);
        loadRootQuestionGrid(questionModal);
    }

    @Override
    public void moveToPreviousQuestion(QuestionModal currentQuestion) {

    }

    @Override
    public void onAllQuestionAnswered(QuestionModal updatedRoot) {
        // TODO:
        // + save result
        /* Example JSON:
        {
            survey_id: [
                option_1_id:  {
                    root_question_modal: {}
                },
                option_2_id: {
                    root_question_modal: {}
                }
            ]
        }
         */

        presenter.alterOptionTosDone(questionModal);

        if (presenter.checkIfAllOptionsHaveBoonAnswered(questionModal.getOptionDataList())) {
            // TODO: finish
        } else {
            loadRootQuestionFragment(questionModal);
        }
    }
}
