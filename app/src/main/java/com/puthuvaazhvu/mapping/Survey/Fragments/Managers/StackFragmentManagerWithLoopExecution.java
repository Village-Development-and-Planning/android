package com.puthuvaazhvu.mapping.Survey.Fragments.Managers;

import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Question.QUESTION_TYPE;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.Survey.Adapters.StatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/22/17.
 */

public class StackFragmentManagerWithLoopExecution extends StackFragmentManagerWithSkipPattern {
    QuestionModal questionModal;
    private ArrayList<QuestionModal> childrenQuestionStack = new ArrayList<>();

    public StackFragmentManagerWithLoopExecution(ViewGroup container
            , QuestionModal questionModal, StatePagerAdapter statePagerAdapter) {
        super(container, questionModal, statePagerAdapter);
        this.questionModal = questionModal;
    }

    public void startMainLoop() {
        // add the root node fragment
        addFragment(questionModal);
    }

    @Override
    public void OnShowNextFragment(QuestionModal currQuestionModal) {
        QuestionModal q = null;
        // if loop question, show gps question (defaults to 0th position).
        if (shouldShowGPSQuestion(currQuestionModal)) {
            q = currQuestionModal.getChildren().get(0);
        }
        // if loop question with gps on top, show grid of children.
        else if (shouldShowGridOfQuestions()) {
            q = currQuestionModal;
        }
        // if normal question. Proceed with populating the stack.
        else {
            // get the next question
            q = getNext(currQuestionModal);
            if (q == null) {
                // pop all the immediately added children fragments
                for (int i = 0; i < childrenQuestionStack.size(); i++) {
                    removeFragment();
                }
                childrenQuestionStack.clear();
                return;
            } else {
                // add to the reference stack so that we can remove later.
                childrenQuestionStack.add(q);
            }
        }

        // push to the stack
        addFragment(q);
    }

    private boolean shouldShowGPSQuestion(QuestionModal currentQuestion) {
        ArrayList<QuestionModal> questionStack = getQuestionListStack();
        return questionStack.size() == 1
                && questionStack.get(0).getQuestionType() == QUESTION_TYPE.LOOP
                && currentQuestion.getTag(Constants.APIDataConstants.TAG_GPS) != null;
    }

    private boolean shouldShowGridOfQuestions() {
        ArrayList<QuestionModal> questionStack = getQuestionListStack();
        return questionStack.size() == 2
                && questionStack.get(0).getQuestionType() == QUESTION_TYPE.LOOP
                && questionStack.get(1).getTag(Constants.APIDataConstants.TAG_GPS) != null;
    }

    @Override
    public void OnShowPreviousFragment(QuestionModal currQuestionModal) {
        super.OnShowPreviousFragment(currQuestionModal);
    }
}
