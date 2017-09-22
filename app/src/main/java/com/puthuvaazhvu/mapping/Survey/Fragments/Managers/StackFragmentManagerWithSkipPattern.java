package com.puthuvaazhvu.mapping.Survey.Fragments.Managers;

import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.Survey.Adapters.StatePagerAdapter;
import com.puthuvaazhvu.mapping.utils.DataHelper;

/**
 * Created by muthuveerappans on 9/22/17.
 */

public class StackFragmentManagerWithSkipPattern extends StackFragmentManager {
    private QuestionModal questionModal;

    public StackFragmentManagerWithSkipPattern(ViewGroup container
            , QuestionModal questionModal
            , StatePagerAdapter statePagerAdapter) {
        super(container, questionModal, statePagerAdapter);
        this.questionModal = questionModal;
    }

    @Override
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
}
