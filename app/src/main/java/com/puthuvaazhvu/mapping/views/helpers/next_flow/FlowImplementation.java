package com.puthuvaazhvu.mapping.views.helpers.next_flow;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.flow.AnswerFlow;
import com.puthuvaazhvu.mapping.modals.flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.flow.ExitFlow;
import com.puthuvaazhvu.mapping.modals.flow.PreFlow;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.utils.deep_copy.DeepCopy;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.ResponseData;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.BackFlowImplementation;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.IBackFlow;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayList;
import java.util.Iterator;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/15/17.
 */

public class FlowImplementation implements IFlow {
    private static final int STACK_SIZE = 50; // max question count the stack can hold

    private final CircularFifoQueue<Answer> stack; // holds the answered questions (irrespective of copies)

    private final ArrayList<Question> toBeRemoved = new ArrayList<>();

    private Question current;

    private IBackFlow iBackFlow;

    public FlowImplementation(Question root) {
        this.current = root;
        stack = new CircularFifoQueue<>(STACK_SIZE);
        this.iBackFlow = new BackFlowImplementation();
    }

    @Override
    public Question getCurrent() {
        return current;
    }

    @Override
    public IFlow finishCurrent() {
        Answer latestAnswer = current.getCurrentAnswer();

        if (latestAnswer != null) {
            Question reference = latestAnswer.getQuestionReference();
            reference.setFinished(true); // set the finished flag to true so we can skip this Q when necessary
            setCurrent(reference.getParent());
        } else {
            throw new IllegalArgumentException("The answers list is empty. Check if the current question "
                    + current.getRawNumber() + " is answered first.");
        }

        return this;
    }

    @Override
    public IFlow moveToIndex(int index) {
        Answer latestAnswer = current.getCurrentAnswer();

        if (latestAnswer != null) {
            try {
                setCurrent(latestAnswer.getChildren().get(index));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("The index " + index + " is not present inside the answers.");
            }
        } else {
            Timber.e("The answers list is empty. Check if the current question "
                    + current.getRawNumber() + " is answered first.");
        }

        return this;
    }

    @Override
    public IFlow update(ResponseData responseData) {
        String rawNumber = responseData.getRawNumber();
        if (!rawNumber.equals(current.getRawNumber())) {
            Timber.e("Number mismatch " + "current: " + current.getRawNumber() + " received: " + rawNumber);
            return this;
        }

        ArrayList<Option> loggedOption = new ArrayList<>();
        loggedOption.addAll(responseData.getResponse());

        long startTime = System.currentTimeMillis();
        Timber.i("Started creation of answers");

        Answer answer = new Answer(
                loggedOption,
                (ArrayList<Question>) DeepCopy.copy(current.getChildren()),
                current);

        Timber.i("Done creation of answers " + (System.currentTimeMillis() - startTime) + "ms");

        current.setAnswer(answer);

        // add answered question to the stack
        stack.add(answer);

        return this;
    }

    @Override
    public ArrayList<Question> emptyToBeRemovedList() {
        ArrayList<Question> result = (ArrayList<Question>) toBeRemoved.clone();
        toBeRemoved.clear();
        return result;
    }

    @Override
    public FlowData getNext() {
        FlowData flowData;
        Question nextQuestion = null;

        do {
            flowData = getNextInternal(current);
            nextQuestion = flowData.question;

            if (nextQuestion == null) {
                // check the exit flow
                ExitFlow exitFlow = current.getFlowPattern().getExitFlow();

                if (exitFlow.getMode() == ExitFlow.Modes.END) {

                    // all question are completed
                    flowData.flowType = FlowType.END;
                    flowData.question = null;
                    break;

                } else if (exitFlow.getMode() == ExitFlow.Modes.PARENT) {

                    Question parent = current.getCurrentAnswer().getQuestionReference().getParent();
                    setCurrent(parent);

                } else if (exitFlow.getMode() == ExitFlow.Modes.LOOP) {

                    AnswerFlow answerFlow = current.getFlowPattern().getAnswerFlow();

                    if (answerFlow.getMode() == AnswerFlow.Modes.OPTION &&
                            shouldSkipBasedOnAnswerScope(current)) {
                        finishCurrent();
                    } else {
                        // return normal flow
                        flowData.flowType = FlowType.SINGLE;
                        flowData.question = current;
                        break;
                    }
                }
            }

        } while (nextQuestion == null);

        setCurrent(flowData.question);

        return flowData;
    }

    @Override
    public IBackFlow.BackFlowData getPrevious() {
        IBackFlow.BackFlowData backFlowData = iBackFlow.getPreviousQuestion(current);

        Question previous = backFlowData.question;

        if (previous != null) {
            setCurrent(previous);
        }

        return backFlowData;
    }

    private FlowData getNextInternal(Question current) {
        FlowData flowData = new FlowData();

        // check the child flow
        ChildFlow childFlow = current.getFlowPattern().getChildFlow();
        ChildFlow.Modes childFlowMode = childFlow.getMode();
        ChildFlow.UI childFlowUI = childFlow.getUiToBeShown();

        if (childFlowMode == ChildFlow.Modes.SELECT) {

            if (childFlowUI == ChildFlow.UI.GRID) {
                // return children grid
                flowData.flowType = FlowType.GRID;
                flowData.question = current;
                return flowData;
            }

        }

        Answer latestAnswer = current.getCurrentAnswer();

        // if the current question does'nt have an answer make it the current one.
        if (latestAnswer == null) {
            flowData.question = current;
            flowData.flowType = FlowType.SINGLE;
            return flowData;
        }

        ArrayList<Question> children = latestAnswer.getChildren();
        Question nextQuestion = null;

        for (Question c : children) {
            if (shouldSkip(c)) {
                addToRemovedList(c);
                continue;
            }

            // set the current question
            nextQuestion = c;
            break;
        }

        flowData.flowType = FlowType.SINGLE;
        flowData.question = nextQuestion;

        return flowData;
    }

    private void addToRemovedList(Question question) {
        toBeRemoved.add(question);

        // to avoid out of memory error
        if (toBeRemoved.size() > 100) {
            Iterator iterator = toBeRemoved.iterator();
            int i = 0;
            while (i < 50) {
                iterator.next();
                iterator.remove();
                i++;
            }
        }
    }


    /* skip when
        + AnswerData scope demands
        + skip pattern matches
    */
    private boolean shouldSkip(Question question) {

        return shouldSkipBasedOnAnswerScope(question) ||
                shouldSkipBasedOnSkipPattern(question) ||
                question.isFinished();
    }

    private boolean shouldSkipBasedOnSkipPattern(Question question) {
        boolean shouldSkip = false;

        // skip pattern
        PreFlow preFlow = question.getFlowPattern().getPreFlow();

        if (preFlow != null) {
            String preFlowQuestionNumber = preFlow.getQuestionSkipRawNumber();
            ArrayList<String> optionsSkip = preFlow.getOptionSkip();

            // search for the question in the answered stack
            Answer preFlowAnswer = getQuestionForSkip(preFlowQuestionNumber);

            if (preFlowAnswer != null) {
                shouldSkip = !doesSkipPatternMatchInQuestion(optionsSkip, preFlowAnswer);
            }
        }

        return shouldSkip;
    }

    private boolean shouldSkipBasedOnAnswerScope(Question question) {
        boolean shouldSkip = false;
        AnswerFlow.Modes answerMode = question.getFlowPattern().getAnswerFlow().getMode();

        if (answerMode == AnswerFlow.Modes.ONCE) {
            shouldSkip = question.getAnswers().size() == 1;
        } else if (answerMode == AnswerFlow.Modes.OPTION) {
            shouldSkip = question.getOptionList().size() == question.getAnswers().size();
        }

        return shouldSkip;
    }

    private Answer getQuestionForSkip(String rawQuestionNumber) {
        for (int i = stack.size() - 1; i >= 0; i--) {
            Answer answer = stack.get(i);
            Question question = answer.getQuestionReference();
            if (question.getRawNumber().equals(rawQuestionNumber)) {
                return answer;
            }
        }
        return null;
    }

    public static boolean doesSkipPatternMatchInQuestion(ArrayList<String> optionsSkipPositions, Answer toCheckAnswer) {
        int correctness = 0;
        ArrayList<Option> loggedOptions = toCheckAnswer.getOptions();
        for (Option option : loggedOptions) {
            for (String o : optionsSkipPositions) {
                if (option.getPosition().equals(o)) {
                    correctness++;
                }
            }
        }
        return correctness == optionsSkipPositions.size();
    }

    private void setCurrent(Question current) {
        this.current = current;
    }

    @VisibleForTesting
    public void setCurrentForTesting(Question current) {
        this.current = current;
    }

}
