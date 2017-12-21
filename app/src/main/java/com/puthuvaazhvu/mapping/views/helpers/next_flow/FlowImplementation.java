package com.puthuvaazhvu.mapping.views.helpers.next_flow;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.flow.AnswerFlow;
import com.puthuvaazhvu.mapping.modals.flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.flow.ExitFlow;
import com.puthuvaazhvu.mapping.modals.flow.PreFlow;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.views.helpers.FlowType;
import com.puthuvaazhvu.mapping.views.helpers.ResponseData;
import com.puthuvaazhvu.mapping.views.helpers.back_navigation.BackNavigation;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/15/17.
 */

public class FlowImplementation implements IFlow {
    private Question current;

    private BackNavigation backNavigation;

    private FlowImplementation() {
        this.backNavigation = new BackNavigation();
    }

    public FlowImplementation(Question root) {
        this();
        setCurrent(root);
    }

    public FlowImplementation(Question root, String snapshotPath) {
        this();
        Question question = Question.moveToQuestion(snapshotPath, root);

        if (question == null) {
            setCurrent(root);
        } else {
            setCurrent(question);
        }
    }

    @Override
    public Question getCurrent() {
        return current;
    }

    @Override
    public IFlow finishCurrent() {
        Answer latestAnswer = current.getLatestAnswer();

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
        Answer latestAnswer = current.getLatestAnswer();

        if (latestAnswer != null) {
            try {
                Question current = latestAnswer.getChildren().get(index);
                setCurrent(current);

                // add to back stack
                backNavigation.addQuestionToStack(current);
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("The index " + index + " is not present inside the answers. "
                        + latestAnswer.toString());
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
        if (rawNumber != null && !rawNumber.equals(current.getRawNumber())) {
            Timber.e("Number mismatch " + "current: " + current.getRawNumber() + " received: " + rawNumber);
            return this;
        }

        ArrayList<Option> loggedOption = new ArrayList<>();
        loggedOption.addAll(responseData.getResponse());

        long startTime = System.currentTimeMillis();
        Timber.i("Started creation of answers");

        Answer answer = new Answer(
                loggedOption,
                current);

        Timber.i("Done creation of answers. Time taken: " + (System.currentTimeMillis() - startTime) + "ms");

        current.setAnswer(answer);

        Timber.i("Answer created info :\n" + answer.toString());

        return this;
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

                if (exitFlow.getMode() == ExitFlow.Modes.PARENT) {

                    Question parent = current.getLatestAnswer().getQuestionReference().getParent();

                    if (parent == null) {
                        // all question are complete
                        flowData.flowType = FlowType.END;
                        flowData.question = null;
                        break;
                    }

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
        backNavigation.addQuestionToStack(flowData.question);

        return flowData;
    }

    @Override
    public IFlow.FlowData getPrevious() {

        if (backNavigation.isStackEmpty()) {
            return FlowData.getFlowData(current);
        }

        Question prev = backNavigation.removeLatest();

        // get the current last question
        Question current = backNavigation.getLatest();

        if (prev != null) {
            removeAnswerOfQuestionFromTree(prev);
            removeAnswerOfQuestionFromTree(current);
        }

        // set the current question
        setCurrent(current);

        // construct and return the flow data
        return FlowData.getFlowData(current);
    }

    public void removeAnswerOfQuestionFromTree(Question toRemove) {
        if (toRemove.isRoot()) {
            Timber.e("Cannot remove the root question. " + toRemove.toString());
            return;
        }

        Answer parentAnswer = toRemove.getParentAnswer();

        for (Question c : parentAnswer.getChildren()) {
            if (c == toRemove) {
                // remove all the answers
                c.removeAnswer();
                Timber.i("Removed answer for question " + c.toString());
                break;
            }
        }
    }

    private FlowData getNextInternal(Question current) {
        FlowData flowData = new FlowData();

        if (current.getFlowPattern() == null) {
            flowData.question = null;
            return flowData;
        }

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

        } else if (childFlowMode == ChildFlow.Modes.TOGETHER) {
            flowData.flowType = FlowType.TOGETHER;
            flowData.question = current;
            return flowData;
        }

        Answer latestAnswer = current.getLatestAnswer();

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
                continue;
            }

            // set the current question
            nextQuestion = c;
            break;
        }

        flowData.flowType = FlowType.SINGLE;
        flowData.question = nextQuestion;

        if (nextQuestion != null) {
            QuestionFlow nextQuestionFlow = nextQuestion.getFlowPattern().getQuestionFlow();
            QuestionFlow.UI nextQuestionUI = nextQuestionFlow.getUiMode();

            ChildFlow nextQuestionChildFlow = nextQuestion.getFlowPattern().getChildFlow();

            if (nextQuestionUI == QuestionFlow.UI.MESSAGE
                    && nextQuestionChildFlow.getMode() == ChildFlow.Modes.TOGETHER) {
                flowData.flowType = FlowType.TOGETHER;
            }
        }

        return flowData;
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

    public static boolean shouldSkipBasedOnSkipPattern(Question question) {
        boolean shouldSkip = false;

        // skip pattern
        PreFlow preFlow = question.getFlowPattern().getPreFlow();

        if (preFlow != null) {
            String preFlowQuestionNumber = preFlow.getQuestionSkipRawNumber();

            if (preFlowQuestionNumber == null) {
                return false;
            }

            ArrayList<String> optionsSkip = preFlow.getOptionSkip();

            Question foundForSkipPattern = question.findAnsweredQuestion(preFlowQuestionNumber);

            if (foundForSkipPattern != null && foundForSkipPattern.getLatestAnswer() != null) {
                shouldSkip = !doesSkipPatternMatchInQuestion(optionsSkip, foundForSkipPattern.getLatestAnswer());
            } else {
                // if no question found then skip
                shouldSkip = true;
            }
        }

        return shouldSkip;
    }

    public static boolean shouldSkipBasedOnAnswerScope(Question question) {
        boolean shouldSkip = false;
        AnswerFlow.Modes answerMode = question.getFlowPattern().getAnswerFlow().getMode();

        if (answerMode == AnswerFlow.Modes.ONCE) {
            shouldSkip = question.getAnswers().size() == 1;
        } else if (answerMode == AnswerFlow.Modes.OPTION) {
            shouldSkip = question.getOptionList().size() == question.getAnswers().size();
        }

        return shouldSkip;
    }

    public static boolean doesSkipPatternMatchInQuestion(ArrayList<String> optionsSkipPositions, Answer toCheckAnswer) {
        if (optionsSkipPositions == null || toCheckAnswer == null) {
            return false;
        }

        boolean skipPatternMatch = false;
        ArrayList<Option> loggedOptions = toCheckAnswer.getOptions();
        for (Option option : loggedOptions) {
            for (String o : optionsSkipPositions) {
                if (option.getPosition() != null && option.getPosition().equals(o)) {
                    skipPatternMatch = true;
                    break;
                }
            }
        }
        return skipPatternMatch;
    }

    @Override
    public void setCurrent(Question current) {
        this.current = current;
    }

}
