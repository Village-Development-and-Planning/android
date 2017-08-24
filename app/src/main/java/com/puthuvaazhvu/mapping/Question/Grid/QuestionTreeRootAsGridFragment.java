package com.puthuvaazhvu.mapping.Question.Grid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragment;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid.RootQuestionsGridHolderFragment;
import com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid.RootQuestionsHolderGridFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.DataHelper;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class QuestionTreeRootAsGridFragment extends Fragment
        implements RootQuestionsHolderGridFragmentCommunicationInterface, QuestionTreeFragmentCommunicationInterface {
    ArrayList<QuestionModal> questionModalList;
    QuestionModal root;
    RootQuestionsGridHolderFragment rootQuestionsGridHolderFragment;
    QuestionTreeFragment questionTreeFragment;
    QuestionTreeRootAsGridFragmentCommunicationInterface communicationInterface;

    public static QuestionTreeRootAsGridFragment getInstance(QuestionModal root) {
        QuestionTreeRootAsGridFragment tagQuestionsFragment = new QuestionTreeRootAsGridFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("question_data", root);

        tagQuestionsFragment.setArguments(bundle);

        return tagQuestionsFragment;
    }

    public void setCommunicationInterface(QuestionTreeRootAsGridFragmentCommunicationInterface communicationInterface) {
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

        if (root == null) {
            throw new RuntimeException("The root question is null. " + QuestionTreeRootAsGridFragment.class);
        }

        questionModalList = root.getChildren();
        loadQuestionGridFragment();
    }

    private void loadQuestionGridFragment() {
        rootQuestionsGridHolderFragment = null;
        rootQuestionsGridHolderFragment = RootQuestionsGridHolderFragment.getInstance(questionModalList);
        rootQuestionsGridHolderFragment.setCommunicationInterface(this);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, rootQuestionsGridHolderFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void loadQuestionTreeFragment(QuestionModal questionModal) {
        questionTreeFragment = null;
        questionTreeFragment = QuestionTreeFragment.getInstance(questionModal);
        questionTreeFragment.setCommunicationInterface(this);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, questionTreeFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onSelectedQuestion(QuestionModal questionModal) {
        loadQuestionTreeFragment(questionModal);
    }

    @Override
    public void onFinished(QuestionModal modifiedQuestionModal) {
        // TODO:
        // + note the questions answered (tag) for end flow.
        // + end the fragment is all questions have been answered.

        DataHelper.modifyQuestionInGiven(root, modifiedQuestionModal);
        loadQuestionGridFragment();
    }
}
