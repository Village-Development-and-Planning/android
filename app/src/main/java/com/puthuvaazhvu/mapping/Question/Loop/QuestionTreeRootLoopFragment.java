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
import com.puthuvaazhvu.mapping.Survey.BaseSurveyFragment;
import com.puthuvaazhvu.mapping.utils.DeepCopy.DeepCopy;

import java.util.ArrayList;

import static com.puthuvaazhvu.mapping.Constants.DEBUG;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public class QuestionTreeRootLoopFragment extends BaseSurveyFragment
        implements QuestionFragmentCommunicationInterface, QuestionTreeRootAsGridFragmentCommunicationInterface {
    QuestionModal questionModal;
    QuestionModal questionModalCopy;
    QuestionFragment questionFragment;
    QuestionTreeRootAsGridFragment questionTreeRootAsGridFragment;
    QuestionTreeRootLoopFragmentPresenter presenter;
    String currentOptionID;
    QuestionTreeRootLoopFragmentCommunicationInterface communicationInterface;

    public static QuestionTreeRootLoopFragment getInstance(QuestionModal questionModal) {
        QuestionTreeRootLoopFragment questionTreeRootLoopFragment = new QuestionTreeRootLoopFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("question_data", questionModal);

        questionTreeRootLoopFragment.setArguments(bundle);

        return questionTreeRootLoopFragment;
    }

    public void setCommunicationInterface(QuestionTreeRootLoopFragmentCommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
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
        questionModalCopy = (QuestionModal) DeepCopy.copy(questionModal);
        loadRootQuestionFragment(questionModalCopy);
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
        if (optionDataList.size() != 1) {
            // TODO: Handle/show error message here.
        } else {
            currentOptionID = optionDataList.get(0).getId();

            presenter.insertOptionDataToMap(optionDataList);
            loadRootQuestionGrid(questionModalCopy);

            getParentCallback().setInfoText(optionDataList.get(0).getText());
        }
    }

    @Override
    public void moveToPreviousQuestion(QuestionModal currentQuestion) {

    }

    @Override
    public void onAllQuestionAnswered(QuestionModal updatedRoot) {
        if (currentOptionID == null) {
            throw new RuntimeException("The option ID is null after the root questions have been answered.");
        }

        presenter.insertQuestionToMap((QuestionModal) DeepCopy.copy(updatedRoot), currentOptionID);

        presenter.alterOptionsToDone(questionModal);

        if (presenter.checkIfAllOptionsHaveBoonAnswered(questionModalCopy.getOptionDataList())) {
            communicationInterface.onLoopFinished(presenter.getOutputMap(questionModalCopy.getQuestionID()));
        } else {
            questionModalCopy = null;
            questionModalCopy = (QuestionModal) DeepCopy.copy(questionModal);
            loadRootQuestionFragment(questionModalCopy);
        }
    }
}
