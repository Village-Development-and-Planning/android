package com.puthuvaazhvu.mapping.Question.Grid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid.GridQuestionModal;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragment;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid.RootQuestionsGridHolderFragment;
import com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid.RootQuestionsHolderGridFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.DataHelper;

import java.util.ArrayList;

import static com.puthuvaazhvu.mapping.Constants.DEBUG;

/**
 * Created by muthuveerappans on 8/24/17.
 */

/*
This class shows all the children of the given question as grid and loops over all children.
This class doesn't show the root. Only shows the children.
 */
public class QuestionTreeRootAsGridFragment extends Fragment
        implements RootQuestionsHolderGridFragmentCommunicationInterface, QuestionTreeFragmentCommunicationInterface {
    ArrayList<QuestionModal> questionModalList;
    QuestionModal root;
    RootQuestionsGridHolderFragment rootQuestionsGridHolderFragment;
    QuestionTreeFragment questionTreeFragment;
    QuestionTreeRootAsGridFragmentCommunicationInterface communicationInterface;
    QuestionTreeAsGridFragmentPresenter presenter;

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
        presenter = new QuestionTreeAsGridFragmentPresenter();

        if (root == null) {
            throw new RuntimeException("The root question is null. " + QuestionTreeRootAsGridFragment.class);
        }

        questionModalList = root.getChildren();
        loadQuestionGridFragment(presenter.convertFrom(questionModalList));
    }

    private void loadQuestionGridFragment(ArrayList<GridQuestionModal> gridQuestionModalArrayList) {
        rootQuestionsGridHolderFragment = null;
        rootQuestionsGridHolderFragment = RootQuestionsGridHolderFragment.getInstance(gridQuestionModalArrayList);
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
    public void onSelectedQuestion(GridQuestionModal questionModal) {
        loadQuestionTreeFragment(questionModal);
    }

    @Override
    public void onFinished(QuestionModal modifiedQuestionModal) {
        presenter.insertEntryIntoMap(modifiedQuestionModal);

        questionModalList = root.getChildren();

        // TODO: Remove the DEBUG Flag.
        if (presenter.checkIfAllQuestionsAreCompleted(questionModalList) || DEBUG) {
            presenter.clearMap();
            communicationInterface.onAllQuestionAnswered(root);
        } else {
            loadQuestionGridFragment(presenter.convertFrom(questionModalList));
        }
    }
}
