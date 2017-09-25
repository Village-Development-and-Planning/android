package com.puthuvaazhvu.mapping.Survey.Fragments.Managers;

import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionType;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;
import com.puthuvaazhvu.mapping.Survey.Adapters.StatePagerAdapter;

import java.util.ArrayList;

import static com.puthuvaazhvu.mapping.Survey.Modals.QuestionType.GPS;
import static com.puthuvaazhvu.mapping.Survey.Modals.QuestionType.GRID_MULTIPLE;
import static com.puthuvaazhvu.mapping.Survey.Modals.QuestionType.LOOP_OPTIONS;

/**
 * Created by muthuveerappans on 9/22/17.
 */

public class StackFragmentManagerWithLoopExecution extends StackFragmentManagerWithSkipPattern {
    private QuestionModal questionModal;
    private ArrayList<QuestionModal> childrenQuestionStack = new ArrayList<>();

    public StackFragmentManagerWithLoopExecution(ViewGroup container
            , QuestionModal questionModal, StatePagerAdapter statePagerAdapter) {
        super(container, questionModal, statePagerAdapter);
        this.questionModal = getQuestionModal();
    }

    @Override
    public void addNextFragment() {
        QuestionModal q = null;
        ArrayList<QuestionModal> dataStack = getDataStack();

        // if loop question, show gps question (defaults to 0th position).
        if (shouldShowGPSQuestion(questionModal)) {
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

    /**
     * Removes all the fragments until the first added fragment
     */
    public void removeTillLoopRootQuestion() {
        ArrayList<QuestionModal> questionStack = getDataStack();
        if (questionStack.size() > 0) {
            for (int i = 0; i < questionStack.size(); i++) {
                removeFragment();
            }
        }
    }
}
