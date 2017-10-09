package com.puthuvaazhvu.mapping.views.helpers.flow;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.Flow.PreFlow;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/9/17.
 */

public class QuestionFlowHelperImpl implements QuestionFlowHelper {
    private final Question root;
    private Question current;
    private ArrayList<Question> toBeRemoved = new ArrayList<>();

    public QuestionFlowHelperImpl(Question root) {
        this.root = root;
        this.current = root;
    }

    @Override
    public Question getNext() {
        if (current == null) {
            throw new IllegalArgumentException("The current question should not be null.");
        }

        // the current question has children
        ChildFlow childFlow = current.getFlowPattern().getChildFlow();
        ChildFlow.Modes childFlowMode = childFlow.getMode();
        Question nextQuestion = null;

        if (childFlowMode == ChildFlow.Modes.CASCADE) {
            ArrayList<Question> children = current.getChildren();
            for (Question q : children) {
                boolean shouldSkip = false; // skip pattern
                PreFlow preFlow = q.getFlowPattern().getPreFlow();
                if (preFlow != null) {
                    String preFlowQuestionNumber = preFlow.getQuestionSkipRawNumber();
                    ArrayList<String> optionsSkip = preFlow.getOptionSkip();

                    if (preFlowQuestionNumber != null && optionsSkip != null) {
                        shouldSkip = shouldSkip(preFlowQuestionNumber, optionsSkip, q); // check for skip
                    }
                }

                if (q.isAnswered() || shouldSkip) {
                    toBeRemoved.add(q);
                    continue;
                }
                nextQuestion = q;
                break;
            }
        } else if (childFlowMode == ChildFlow.Modes.SELECT) {
            nextQuestion = current;
        }

        if (nextQuestion == null) {
            current = current.getParent();
            current = getNext();
        } else {
            current = nextQuestion;
        }
        return current;
    }

    @Override
    public void setCurrent(Question currentQuestion) {
        this.current = currentQuestion;
    }

    @Override
    public Question getCurrent() {
        return current;
    }

    @Override
    public ArrayList<Question> clearToBeRemovedList() {
        ArrayList<Question> result = new ArrayList<>();
        for (int i = 0; i < toBeRemoved.size(); i++) {
            result.add(toBeRemoved.remove(i));
        }
        return result;
    }

    public boolean shouldSkip(String rawNumber, ArrayList<String> optionPositions, Question current) {
        int correctness = 0; // correctness should be equal to the option positions count to avoid skip.
        Question parent = getQuestionReverse(rawNumber, current);
        if (parent != null) {
            int answerCountForParent = parent.getAnswer().size();
            if (answerCountForParent > 0) {
                Answer parentAnswer = parent.getAnswer().get(answerCountForParent - 1); // get the last logged answer
                ArrayList<Option> loggedOptions = parentAnswer.getOptions();
                if (loggedOptions != null) {
                    for (Option lo : loggedOptions) {
                        for (String op : optionPositions) {
                            if (lo.getPosition().equals(op)) {
                                correctness += 1;
                            }
                        }
                    }
                }
            } else {
                correctness = 0;
            }
        }
        return correctness != optionPositions.size();
    }

    /**
     * Helper to get the question traversing from the bottom. Recursive
     *
     * @param rawNumber The raw number of the question to search for
     * @param current   The current question in action
     * @return The resultant question found
     */
    public Question getQuestionReverse(String rawNumber, Question current) {
        if (current.getRawNumber().equals(rawNumber)) {
            return current;
        }
        Question parent = current.getParent();
        if (parent == null) {
            return null; // not found
        }
        for (Question c : parent.getChildren()) {
            if (!rawNumber.equals(c.getRawNumber())) {
                continue;
            }
            return getQuestionReverse(rawNumber, parent);
        }
        return getQuestionReverse(rawNumber, parent);
    }
}
