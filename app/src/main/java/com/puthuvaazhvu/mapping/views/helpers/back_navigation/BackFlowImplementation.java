package com.puthuvaazhvu.mapping.views.helpers.back_navigation;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.flow.ExitFlow;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/24/17.
 */

public class BackFlowImplementation implements IBackFlow {

    @Override
    public BackFlowData getPreviousQuestion(Question current) {

        BackFlowData backFlowData = new BackFlowData();

        Question previous = getPreviousQuestionInternal(current);

        if (previous != null) {
            previous = eraseTheLastAnswer(previous);
        }

        backFlowData.question = previous;

        return backFlowData;
    }

    private Question getPreviousQuestionInternal(Question current) {

        // for grid the answers will be already logged.
        if (current.getFlowPattern().getChildFlow().getUiToBeShown() == ChildFlow.UI.GRID) {
            try {
                eraseTheLastAnswer(current);
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }

        Question parent = current.getParent();

        if (parent == null) {
            throw new IllegalArgumentException("This is the ROOT question.");
        }

        if (parent.isRoot()) {
            return null;
        }

        ChildFlow childFlow = parent.getFlowPattern().getChildFlow();
        Question previous = null;

        if (childFlow.getMode() == ChildFlow.Modes.CASCADE) {

            previous = getPreviousQuestionTraversingBack(parent, current);

            if (previous == null) {
                previous = parent;
            }

        }

        if (previous != null &&
                previous.getFlowPattern().getExitFlow().getMode() == ExitFlow.Modes.LOOP) {
            return null;
        }

        return previous;
    }

    private Question getPreviousQuestionTraversingBack(Question node, Question current) {
        Question previous = null;

        Answer nodeLatestAnswer = getLatestAnswer(node);

        for (Question child : nodeLatestAnswer.getChildren()) {
            if (child.getRawNumber().equals(current.getRawNumber())) {
                return previous;
            }
            previous = child;
        }

        return previous;
    }

    private Question eraseTheLastAnswer(Question question) {
        int answersCount = question.getAnswers().size();

        if (answersCount > 0) {

            Answer lastAnswer = question.getAnswers().remove(answersCount - 1);
            Timber.i("Removed answer: " + lastAnswer.toString());
            Timber.i("New answer count: " + question.getAnswers().size());

        } else {
            throw new IllegalArgumentException("The question " + question.getRawNumber() + " does'nt have an answer.");
        }

        return question;
    }

    private Answer getLatestAnswer(Question question) {
        int answersCount = question.getAnswers().size();

        if (answersCount > 0) {
            return question.getAnswers().get(answersCount - 1);
        } else {
            throw new IllegalArgumentException("The question " + question.getRawNumber() + " does'nt have an answer.");
        }
    }
}
