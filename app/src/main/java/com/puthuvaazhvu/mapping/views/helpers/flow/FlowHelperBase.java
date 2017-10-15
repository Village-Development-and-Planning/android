package com.puthuvaazhvu.mapping.views.helpers.flow;

import android.support.annotation.VisibleForTesting;

import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/13/17.
 */

public abstract class FlowHelperBase {
    private final Question root;

    protected Question current;

    private ArrayList<Question> toBeRemoved = new ArrayList<>();

    public FlowHelperBase(Question root) {
        this.root = root;
        this.current = root;
    }

    public abstract FlowData getNext();

    public abstract FlowHelperBase update(QuestionData questionData);

    public abstract FlowHelperBase moveToIndex(int index);

    public abstract FlowHelperBase finishCurrentQuestion();

    public Question getRoot() {
        return root;
    }

    public Question getCurrent() {
        return current;
    }

    public ArrayList<Question> clearToBeRemovedList() {
        ArrayList<Question> result = (ArrayList<Question>) toBeRemoved.clone();
        toBeRemoved.clear();
        return result;
    }

    protected void setCurrent(Question current) {
        this.current = current;
    }

    public void addToRemovedList(Question question) {
        toBeRemoved.add(question);
    }

    public static Option getOption(Question question, String optionID) {
        ArrayList<Option> options = question.getOptionList();
        for (Option o : options) {
            if (o.getId().equals(optionID)) {
                return o;
            }
        }
        return null;
    }

    @VisibleForTesting
    public void setCurrentForTesting(Question question) {
        current = question;
    }

    public static class FlowData {
        public Question question;
        public FlowType flowType;
    }
}
