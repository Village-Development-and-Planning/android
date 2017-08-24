package com.puthuvaazhvu.mapping.Question.TagQuestions;

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
import com.puthuvaazhvu.mapping.Question.QuestionsGridFragment.QuestionGridFragment;
import com.puthuvaazhvu.mapping.Question.QuestionsGridFragment.QuestionGridFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.R;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class TagQuestionsFragment extends Fragment
        implements QuestionGridFragmentCommunicationInterface, QuestionTreeFragmentCommunicationInterface {
    ArrayList<QuestionModal> questionModalList;
    QuestionGridFragment questionGridFragment;
    QuestionTreeFragment questionTreeFragment;

    public static TagQuestionsFragment getInstance(ArrayList<QuestionModal> questionModalList) {
        TagQuestionsFragment tagQuestionsFragment = new TagQuestionsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("question_data_list", questionModalList);

        tagQuestionsFragment.setArguments(bundle);

        return tagQuestionsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty_frame_for_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        questionModalList = getArguments().getParcelableArrayList("question_data_list");
        loadQuestionGridFragment();
    }

    private void loadQuestionGridFragment() {
        questionGridFragment = null;
        questionGridFragment = QuestionGridFragment.getInstance(questionModalList);
        questionGridFragment.setCommunicationInterface(this);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, questionGridFragment);
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
        // + update questions data.
        // + note the questions answered (tag) for end flow.
        loadQuestionGridFragment();
    }
}
