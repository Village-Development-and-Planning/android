package com.puthuvaazhvu.mapping.Question.Grid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Survey.Modals.GridQuestionModal;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragment;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid.RootQuestionsGridHolderFragment;
import com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid.RootQuestionsHolderGridFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.DeepCopy.DeepCopy;
import com.puthuvaazhvu.mapping.utils.Utils;

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
        implements RootQuestionsHolderGridFragmentCommunicationInterface
        , QuestionTreeFragmentCommunicationInterface
        , View.OnClickListener {
    QuestionModal root;
    QuestionModal rootCopy;
    RootQuestionsGridHolderFragment rootQuestionsGridHolderFragment;
    QuestionTreeFragment questionTreeFragment;
    QuestionTreeRootAsGridFragmentCommunicationInterface communicationInterface;
    QuestionTreeAsGridFragmentPresenter presenter;
    Button endGPSButton;
    View bottomContainer;

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
        return inflater.inflate(R.layout.grid_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        endGPSButton = view.findViewById(R.id.end_gps_button);
        endGPSButton.setOnClickListener(this);

        bottomContainer = view.findViewById(R.id.bottom_container);

        root = getArguments().getParcelable("question_data");
        rootCopy = (QuestionModal) DeepCopy.copy(root);
        presenter = new QuestionTreeAsGridFragmentPresenter();

        if (root == null) {
            throw new RuntimeException("The root question is null. " + QuestionTreeRootAsGridFragment.class);
        }

        loadQuestionGridFragment(presenter.convertFrom(rootCopy.getChildren()));
    }

    private void loadQuestionGridFragment(ArrayList<GridQuestionModal> gridQuestionModalArrayList) {
        rootQuestionsGridHolderFragment = null;
        rootQuestionsGridHolderFragment = RootQuestionsGridHolderFragment.getInstance(gridQuestionModalArrayList);
        rootQuestionsGridHolderFragment.setCommunicationInterface(this);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, rootQuestionsGridHolderFragment);
        fragmentTransaction.commitAllowingStateLoss();

        bottomContainer.setVisibility(View.VISIBLE);
    }

    private void loadQuestionTreeFragment(QuestionModal questionModal) {
        questionTreeFragment = null;
        questionTreeFragment = QuestionTreeFragment.getInstance(questionModal);
        questionTreeFragment.setCommunicationInterface(this);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, questionTreeFragment);
        fragmentTransaction.commitAllowingStateLoss();

        bottomContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSelectedQuestion(GridQuestionModal questionModal) {
        if (presenter.isIterationAllowed(questionModal)) {
            presenter.setIDToQuestion(questionModal, Utils.generateRandomUUID());
            loadQuestionTreeFragment(questionModal);
        } else {
            Utils.showErrorMessage(Constants.ErrorMessages.ITERATION_ERROR, getContext());
        }
    }

    @Override
    public void onFinished(QuestionModal modifiedQuestionModal) {
        presenter.addQuestionToResult((QuestionModal) DeepCopy.copy(modifiedQuestionModal));
        rootCopy = (QuestionModal) DeepCopy.copy(root);
        loadQuestionGridFragment(presenter.convertFrom(rootCopy.getChildren()));

        //presenter.insertEntryIntoMap(modifiedQuestionModal);

//        questionModalList = root.getChildren();
//
//        // TODO: Remove the DEBUG Flag.
//        if (presenter.checkIfAllQuestionsAreCompleted(questionModalList) || DEBUG) {
//            presenter.clearMap();
//            communicationInterface.onAllQuestionAnswered(root);
//        } else {
//            loadQuestionGridFragment(presenter.convertFrom(questionModalList));
//        }
    }

    @Override
    public void onChildFragmentPop() {
        loadQuestionGridFragment(presenter.convertFrom(rootCopy.getChildren()));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.end_gps_button) {

            // TODO: Record GPS

            // TODO: Remove the DEBUG Flag.
            if (presenter.checkIfAllQuestionsAreAnswered(root) || DEBUG) {
                QuestionModal updatedRoot = presenter.getResult(root);
                communicationInterface.onAllQuestionAnswered(updatedRoot);
            } else {
                Utils.showErrorMessage(Constants.ErrorMessages.SURVEY_INCOMPLETE, getContext());
            }
        }
    }
}
