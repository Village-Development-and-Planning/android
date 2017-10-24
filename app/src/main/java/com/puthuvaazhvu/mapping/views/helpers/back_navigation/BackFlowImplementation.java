package com.puthuvaazhvu.mapping.views.helpers.back_navigation;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Question;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/24/17.
 */

public class BackFlowImplementation implements IBackFlow {

    @Override
    public BackFlowData getPreviousQuestion(Question current) {

        BackFlowData backFlowData = new BackFlowData();

        Question parent = current.getParent();

        if (parent == null) {
            throw new IllegalArgumentException("This is the ROOT question.");
        }

        if (parent.isRoot()) {

            // don't go to the ROOT question. Throw an error instead, saying user cannot go to the previous question.
            backFlowData.isError = true;
            backFlowData.question = current;

        } else {
            backFlowData.question = eraseTheLastAnswer(parent);
            backFlowData.isError = false;
        }

        return backFlowData;
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
}
