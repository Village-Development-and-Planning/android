package com.puthuvaazhvu.mapping.Survey.Fragments.Managers;

import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;
import com.puthuvaazhvu.mapping.Survey.Adapters.StatePagerAdapter;
import com.puthuvaazhvu.mapping.utils.DataHelper;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/22/17.
 */


public class StackFragmentManagerWithSkipPattern extends StackFragmentManager<QuestionModal> {
    private QuestionModal questionModal;

    public StackFragmentManagerWithSkipPattern(ViewGroup container
            , QuestionModal questionModal
            , StatePagerAdapter statePagerAdapter) {
        super(container, statePagerAdapter);
        this.questionModal = questionModal;
    }

    public QuestionModal removeQuestion() {
        return removeFragment();
    }

    protected void addNextFragment() {
        // get the next question
        QuestionModal nextData = getNext(questionModal);
        if (nextData == null) {

            // pop all the added fragments
            removeAllFragments();
            return;
        }
        // push to the stack
        addFragment(nextData);
    }

    /**
     * Get the next data.
     *
     * @param node The node to search for the next question.
     * @return The question to show.
     */
    public QuestionModal getNext(QuestionModal node) {
        if (!node.isAnswered() && DataHelper.shouldShowQuestion(questionModal, node.getInfo())) {
            return node;
        }
        QuestionModal next = null;
        for (QuestionModal q : node.getChildren()) {
            next = getNext(q);
            if (next != null) {
                break;
            }
        }
        return next;
    }

    public QuestionModal getQuestionModal() {
        return questionModal;
    }
}
