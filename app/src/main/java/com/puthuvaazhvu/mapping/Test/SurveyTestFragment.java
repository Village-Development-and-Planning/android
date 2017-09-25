package com.puthuvaazhvu.mapping.Test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Modals.Question;
import com.puthuvaazhvu.mapping.Modals.Survey;
import com.puthuvaazhvu.mapping.Survey.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.Grid.QuestionTreeRootAsGridFragment;
import com.puthuvaazhvu.mapping.Question.Grid.QuestionTreeRootAsGridFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Question.Loop.QuestionTreeRootLoopFragment;
import com.puthuvaazhvu.mapping.Question.Loop.QuestionTreeRootLoopFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragment;
import com.puthuvaazhvu.mapping.Question.QuestionTree.QuestionTreeFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.Question.SingleQuestion.QuestionFragment;
import com.puthuvaazhvu.mapping.Question.SingleQuestion.QuestionFragmentCommunicationInterface;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.utils.ModalAdapters;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by muthuveerappans on 8/30/17.
 */

public class SurveyTestFragment extends Fragment
        implements QuestionFragmentCommunicationInterface,
        QuestionTreeFragmentCommunicationInterface,
        QuestionTreeRootAsGridFragmentCommunicationInterface,
        QuestionTreeRootLoopFragmentCommunicationInterface {
    Survey survey;
    QuestionFragment questionFragment;
    QuestionTreeFragment questionTreeFragment;
    QuestionTreeRootAsGridFragment questionTreeRootAsGridFragment;
    QuestionTreeRootLoopFragment questionTreeRootLoopFragment;

    private View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.b1:
                    testSingleQuestionFragment();
                    break;
                case R.id.b2:
                    testQuestionTreeFragment();
                    break;
                case R.id.b3:
                    testQuestionGridFragment();
                    break;
                case R.id.b4:
                    testQuestionLoopFragment();
                    break;
            }
        }
    };

    public static SurveyTestFragment getInstance(Survey survey) {
        SurveyTestFragment surveyTestFragment = new SurveyTestFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("survey_data", survey);
        surveyTestFragment.setArguments(bundle);

        return surveyTestFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        survey = getArguments().getParcelable("survey_data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_survey_for_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.b1).setOnClickListener(buttonClick);
        view.findViewById(R.id.b2).setOnClickListener(buttonClick);
        view.findViewById(R.id.b3).setOnClickListener(buttonClick);
        view.findViewById(R.id.b4).setOnClickListener(buttonClick);
    }

    private void testSingleQuestionFragment() {
        QuestionModal questionModal = getAnyDummyQuestion();

        if (questionModal == null) {
            throw new RuntimeException("The question data is null");
        }

        questionFragment = null;
        questionFragment = QuestionFragment.getInstance(questionModal);
        questionFragment.setCommunicationInterface(this);

        replaceFragment(questionFragment);
    }

    private void testQuestionTreeFragment() {
        QuestionModal questionModal = getAnyQuestionTree();

        if (questionModal == null) {
            throw new RuntimeException("The question data is null");
        }

        questionTreeFragment = null;
        questionTreeFragment = QuestionTreeFragment.getInstance(questionModal);
        questionTreeFragment.setCommunicationInterface(this);

        replaceFragment(questionTreeFragment);
    }

    private void testQuestionLoopFragment() {
        QuestionModal questionModal = getQuestionRoot();

        if (questionModal == null) {
            throw new RuntimeException("The question data is null");
        }

        questionTreeRootLoopFragment = null;
        questionTreeRootLoopFragment = QuestionTreeRootLoopFragment.getInstance(questionModal);
        questionTreeRootLoopFragment.setCommunicationInterface(this);

        replaceFragment(questionTreeRootLoopFragment);
    }

    private void testQuestionGridFragment() {
        QuestionModal questionModal = getQuestionRoot();

        if (questionModal == null) {
            throw new RuntimeException("The question data is null");
        }

        questionTreeRootAsGridFragment = null;
        questionTreeRootAsGridFragment = QuestionTreeRootAsGridFragment.getInstance(questionModal);
        questionTreeRootAsGridFragment.setCommunicationInterface(this);

        replaceFragment(questionTreeRootAsGridFragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private QuestionModal getQuestionRoot() {
        ArrayList<Question> questions = survey.getQuestionList();
        for (Question question : questions) {
            if (question.getType().equals("MAIN_LOOP_DYNAMIC_OPTIONS")) {
                return ModalAdapters.getAsQuestionModal(question, Constants.isTamil);
            }
        }
        return null;
    }

    private QuestionModal getAnyQuestionTree() {
        ArrayList<Question> questions = survey.getQuestionList();
        for (Question question : questions) {
            if (question.getType().equals("MAIN_LOOP_DYNAMIC_OPTIONS")) {
                for (Question q : question.getChildren()) {
                    if (q.getRawNumber().equals("2.3")) {
                        return ModalAdapters.getAsQuestionModal(q, Constants.isTamil);
                    }
                }
            }
        }
        return null;
    }

    private QuestionModal getAnyDummyQuestion() {
        ArrayList<Question> questions = survey.getQuestionList();
        for (Question question : questions) {
            if (question.getType().equals("DUMMY")) {
                Question randomQuestion = question.getChildren().get(0);
                return ModalAdapters.getAsQuestionModal(randomQuestion, Constants.isTamil);
            }
        }
        return null;
    }

    // method of QuestionTreeFragmentCommunicationInterface
    @Override
    public void onFinished(QuestionModal modifiedQuestionModal) {
        Log.i(Constants.LOG_TAG, "The question tree finished for ID : " + modifiedQuestionModal.getQuestionID());
        Toast.makeText(getContext(), "Survey finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChildFragmentPop() {
        // NOT IMPLEMENTED
    }

    // method of QuestionTreeRootAsGridFragment
    @Override
    public void onAllQuestionAnswered(QuestionModal updatedRoot) {
        Log.i(Constants.LOG_TAG, "The Question grid is completed for ID : " + updatedRoot.getQuestionID());
        Toast.makeText(getContext(), "Survey finished", Toast.LENGTH_SHORT).show();
    }

    // method of QuestionTreeRootLoopFragment
    @Override
    public void onLoopFinished(HashMap<String, HashMap<String, QuestionModal>> result) {
        Log.i(Constants.LOG_TAG, "The question loop is completed.");
        Toast.makeText(getContext(), "Survey finished", Toast.LENGTH_SHORT).show();
    }

    // method of QuestionFragment
    @Override
    public void moveToNextQuestion(QuestionModal currentQuestion, ArrayList<OptionData> optionDataList) {
        Log.i(Constants.LOG_TAG, "Next button pressed from question : " + currentQuestion.getQuestionID());
    }

    // method of QuestionFragment
    @Override
    public void moveToPreviousQuestion(QuestionModal currentQuestion) {
        Log.i(Constants.LOG_TAG, "Back button pressed from question : " + currentQuestion.getQuestionID());
    }
}
