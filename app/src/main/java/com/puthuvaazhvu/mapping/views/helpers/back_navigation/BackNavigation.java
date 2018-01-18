package com.puthuvaazhvu.mapping.views.helpers.back_navigation;

import com.puthuvaazhvu.mapping.modals.Question;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 12/21/17.
 */

@Deprecated
public class BackNavigation {
    private final ArrayList<Question> questionStack;

    public BackNavigation() {
        questionStack = new ArrayList<>();
    }

    public void addQuestionToStack(Question question) {
        questionStack.add(question);
    }

    public boolean isStackEmpty() {
        return questionStack.isEmpty();
    }

    public Question getLatest() {
        if (questionStack.isEmpty()) return null;
        return questionStack.get(getLastIndex());
    }

    public Question removeLatest() {
        if (questionStack.isEmpty()) {
            return null;
        }

        int indexToRemove = getLastIndex();


        // always maintain min of 2 elements in the array
        // this is for the ROOT question.
        if (indexToRemove > 1) {
            return questionStack.remove(indexToRemove);
        }

        return null;
    }

    private int getLastIndex() {
        int lastIndex = questionStack.size() - 1;
        if (lastIndex < 0) lastIndex = 0;
        return lastIndex;
    }
}
