package com.puthuvaazhvu.mapping.Survey.Fragments.Managers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.Survey.Adapters.StatePagerAdapter;
import com.puthuvaazhvu.mapping.Survey.Fragments.DynamicTypes.DynamicFragmentTypeCommunicationInterface;
import com.puthuvaazhvu.mapping.Survey.Fragments.DynamicTypes.NormalQuestionFragment;
import com.puthuvaazhvu.mapping.utils.LinkedList.LinkListStack;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/21/17.
 */

/*
This helper class arranges the child fragments in a stack under the given fragment manager.
Pops the added fragments after all children questions are answered.
- 2.2.3 -
- 2.2.2.1 -
- 2.2.2 -
- 2.2.1 -
- 2.2 -
 */
public class StackFragmentManager implements DynamicFragmentTypeCommunicationInterface {
    private final ViewGroup container;
    private final QuestionModal questionModal;
    private final StatePagerAdapter statePagerAdapter;
    private ArrayList<QuestionModal> questionListStack = new ArrayList<>();

    public StackFragmentManager(ViewGroup container
            , QuestionModal questionModal
            , StatePagerAdapter statePagerAdapter) {
        this.statePagerAdapter = statePagerAdapter;
        this.container = container;
        this.questionModal = questionModal;
    }

    @Override
    public void OnShowNextFragment(QuestionModal currQuestionModal) {
        // get the next question
        QuestionModal nextQuestion = getNext(currQuestionModal);
        if (nextQuestion == null) {
            // pop all the added fragments
            removeAllFragments();
            return;
        }
        // push to the stack
        addFragment(nextQuestion);
    }

    @Override
    public void OnShowPreviousFragment(QuestionModal currQuestionModal) {
        // pop action
        removeFragment();
    }

    public void push(QuestionModal questionModal) {
        questionListStack.add(questionModal);
    }

    public QuestionModal pop() {
        int indexToBeRemoved = questionListStack.size() - 1;
        if (indexToBeRemoved < 0) {
            // the list is empty
            return null;
        }
        // remove the last element. Replicate a linked-list.
        return questionListStack.remove(questionListStack.size() - 1);
    }

    protected void addFragment(QuestionModal questionModal) {
        push(questionModal);
        Fragment f = (Fragment) statePagerAdapter.instantiateItem(container, questionModal);
        statePagerAdapter.finishUpdate(container);
        statePagerAdapter.setPrimaryItem(container, questionModal, f);
    }

    public void removeAllFragments() {
        QuestionModal questionModal;
        while ((questionModal = pop()) != null) {
            statePagerAdapter.destroyItem(container, questionModal);
        }
    }

    protected void removeFragment() {
        QuestionModal questionModal = pop();
        if (questionModal != null)
            statePagerAdapter.destroyItem(container, questionModal);
    }

    /**
     * Recursively get the next question to be answered.
     *
     * @param node The node to search for the next question.
     * @return The question to show.
     */
    public QuestionModal getNext(QuestionModal node) {
        if (!node.isAnswered()) {
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

    public ArrayList<QuestionModal> getQuestionListStack() {
        return questionListStack;
    }

//    private class StackFragmentStatePagerAdapter extends StatePagerAdapter {
//
//        public StackFragmentStatePagerAdapter(FragmentManager fragmentManager) {
//            super(fragmentManager);
//        }
//
//        @Override
//        public Fragment getItem(Object key) {
//            QuestionModal questionModal = (QuestionModal) key;
//            return NormalQuestionFragment.getInstance(questionModal);
//        }
//    }
}
