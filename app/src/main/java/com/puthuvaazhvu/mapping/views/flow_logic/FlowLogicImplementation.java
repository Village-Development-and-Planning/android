package com.puthuvaazhvu.mapping.views.flow_logic;

import android.support.annotation.VisibleForTesting;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.AnswerFlow;
import com.puthuvaazhvu.mapping.modals.flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.flow.ExitFlow;
import com.puthuvaazhvu.mapping.modals.flow.PreFlow;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 16/01/18.
 */

public class FlowLogicImplementation extends FlowLogic {
    private BackStack backStack;

    public FlowLogicImplementation() {
        super();
        backStack = new BackStack();
    }

    public FlowLogicImplementation(Question root) {
        this();
        setCurrent(root, FlowData.FlowUIType.DEFAULT);
    }

    public FlowLogicImplementation(Question root, String snapshotPath) {
        this();
        Question question = Question.moveToQuestion(snapshotPath, root);

        if (question == null) {
            setCurrent(root, FlowData.FlowUIType.DEFAULT);
        } else {
            // answer for the parent question would have been already added in the pre question construction flow.
            Answer parentAnswer = question.getParentAnswer();
            Question parent = parentAnswer.getQuestionReference();

            // change the visible child index position
            int index = parent.getIndexOfChild(question);

            if (index >= 0) {
                parentAnswer.setCurrentChildIndex(index);
            }

            setCurrent(question, FlowData.FlowUIType.DEFAULT);
        }
    }

    @Override
    public void setCurrent(Question question, FlowData.FlowUIType flowUIType) {
        this.currentFlowData.question = question;
        this.currentFlowData.flowType = flowUIType;
    }

    @Override
    public FlowData getCurrent() {
        return currentFlowData;
    }

    @Override
    public FlowLogic finishCurrent() {
        Answer latestAnswer = currentFlowData.question.getLatestAnswer();

        if (latestAnswer != null) {
            Question reference = latestAnswer.getQuestionReference();
            reference.setFinished(true); // set the finished flag to true so we can skip this Q when necessary
            setCurrent(reference.getParent(), FlowData.FlowUIType.DEFAULT);
        } else {
            throw new IllegalArgumentException("The answers list is empty. Check if the current question "
                    + currentFlowData.question.getRawNumber() + " is answered first.");
        }

        return this;
    }

    @Override
    public FlowLogic moveToIndexInChild(int index) {
        Answer latestAnswer = currentFlowData.question.getLatestAnswer();

        if (latestAnswer != null) {
            try {
                Question current = latestAnswer.getChildren().get(index);

                // add a dummy answer
                addAnswer(Question.dummyOptions(), current);

                setCurrent(current, FlowData.FlowUIType.DEFAULT);

                // add to back stack
                addEntryToBackStack(currentFlowData);
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("The index " + index + " is not present inside the answers. "
                        + latestAnswer.toString());
            }
        } else {
            Timber.e("The answers list is empty. Check if the current question "
                    + currentFlowData.question.getRawNumber() + " is answered first.");
        }

        return this;
    }

    @Override
    public FlowLogic update(ArrayList<Option> response) {
        update(response, currentFlowData.question);
        return this;
    }

    @Override
    public FlowLogic update(ArrayList<Option> response, Question question) {
        if (question.getAnswers().size() <= 0) {
            // add a new dummy answer if answers list is empty.
            Answer answer = new Answer(Question.dummyOptions(), question, System.currentTimeMillis());
            question.setAnswer(answer);
            Timber.i("Answer count for the question: \n" + question.toString()
                    + " is 0, so adding a new one.\n" + answer.toString());
        }

        long startTime = System.currentTimeMillis();

        Answer answer = question.getLatestAnswer();
        answer.setOptions(response);
        answer.setTimeStamp(startTime);

        Timber.i("Answer updated info :\n" + answer.toString());
        return this;
    }

    @Override
    public FlowData getPrevious() {

        // first remove the last question
        if (backStack.removeLatest() == null) {
            return currentFlowData;
        }

        // get the current last question
        FlowData current = backStack.getLatest();

        if (current != null) {
            Answer latestAnswer = current.question.getLatestAnswer();
            Answer parentAnswer = current.question.getParentAnswer();
            Question parent = parentAnswer.getQuestionReference();

            // change the visible child index position
            int index = parent.getIndexOfChild(current.question);

            if (index >= 0) {
                parentAnswer.setCurrentChildIndex(index);
            }

            // reset the current visible child index.
            latestAnswer.resetCurrentChildIndex();

            // set the current question
            setCurrent(current.question, current.flowType);
            return current;
        }

        return currentFlowData;
    }

    @Override
    public FlowData getNext() {

        FlowData nextFlowData = null;
        Question current = currentFlowData.question;

        do {

            nextFlowData = childFlow(current);

            // if next question is null check for the exit flow in that question
            if (nextFlowData.question == null) {
                FlowData exitFlowData = exitFlow(current);
                current = exitFlowData.question;
                if (exitFlowData.flowType == FlowData.FlowUIType.END) {
                    nextFlowData = exitFlowData;
                    return nextFlowData;
                } else {
                    if (exitFlowData.flowType == FlowData.FlowUIType.LOOP)
                        nextFlowData = exitFlowData;
                }
            }

        } while (nextFlowData.question == null);

        addAnswer(Question.dummyOptions(), nextFlowData.question);

        setCurrent(nextFlowData.question, nextFlowData.flowType);

        QuestionFlow questionFlow = nextFlowData.question.getFlowPattern().getQuestionFlow();
        if (questionFlow != null && questionFlow.getUiMode() == QuestionFlow.UI.NONE) {
            return getNext();
        }

//        ChildFlow childFlow = nextFlowData.question.getFlowPattern().getChildFlow();
//        ChildFlow.Modes childFlowMode = childFlow.getMode();
//        ChildFlow.UI childFlowUI = childFlow.getUiToBeShown();

        // if the type is shown together then pre populate the children question with dummy answers
//        if (childFlowMode == ChildFlow.Modes.TOGETHER) {
//            addDummyAnswersToChildren(nextFlowData.question);
//        }

//        if (childFlowMode == ChildFlow.Modes.SELECT && childFlowUI == ChildFlow.UI.GRID) {
//            addDummyAnswersToChildren(nextFlowData.question);
//        } else if (childFlowMode == ChildFlow.Modes.TOGETHER) {
//            addDummyAnswersToChildren(nextFlowData.question);
//        }

        // add to back stack
        addEntryToBackStack(nextFlowData);

        return nextFlowData;
    }

    @VisibleForTesting
    public FlowData exitFlow(Question question) {
        FlowData flowData = new FlowData();
        flowData.question = null;
        flowData.flowType = FlowData.FlowUIType.DEFAULT;

        ExitFlow exitFlow = question.getFlowPattern().getExitFlow();

        if (exitFlow.getMode() == ExitFlow.Modes.END || question.isRoot()) {
            flowData.flowType = FlowData.FlowUIType.END;
            return flowData;
        } else if (exitFlow.getMode() == ExitFlow.Modes.PARENT) {

            Question parent = question.getParentAnswer().getQuestionReference();

            if (parent == null) {
                // all question are complete
                flowData.flowType = FlowData.FlowUIType.END;
                return flowData;
            }

        } else if (exitFlow.getMode() == ExitFlow.Modes.LOOP) {

            AnswerFlow answerFlow = question.getFlowPattern().getAnswerFlow();

            if (answerFlow.getMode() == AnswerFlow.Modes.OPTION &&
                    shouldSkipBasedOnAnswerScope(question)) {

                // finish the current question
                question.setFinished(true);
            } else {
                flowData.question = question;
                flowData.flowType = FlowData.FlowUIType.LOOP;
                return flowData;
            }
        }

        Question parent = question.getParentAnswer().getQuestionReference();
        question.getParentAnswer().incrementCurrentChildIndex();
        flowData.question = parent;
        return flowData;
    }

    @VisibleForTesting
    public FlowData childFlow(Question question) {
        FlowData flowData = new FlowData();
        flowData.question = null;
        flowData.flowType = FlowData.FlowUIType.DEFAULT;

        try {
            ChildFlow childFlow = question.getFlowPattern().getChildFlow();
            ChildFlow.Modes childFlowMode = childFlow.getMode();
            ChildFlow.UI childFlowUI = childFlow.getUiToBeShown();

            if ((childFlowMode == ChildFlow.Modes.SELECT && childFlowUI == ChildFlow.UI.GRID)
                    || childFlowMode == ChildFlow.Modes.TOGETHER) {
                // return the same question for these modes. Only UI is different
                flowData.flowType = childFlowUI == ChildFlow.UI.GRID
                        ? FlowData.FlowUIType.GRID : FlowData.FlowUIType.TOGETHER;
                flowData.question = question;
                return flowData;
            }

            // cascade mode

            // if the answer is empty then shown this question
            if (question.getAnswers().isEmpty()) {
                flowData.question = question;
                return flowData;
            }

            Answer latestAnswer = question.getLatestAnswer();

            // if there are no child questions, we have to get the next question
            if (latestAnswer.isCurrentChildIndexOutOfBounds()) {
                latestAnswer.resetCurrentChildIndex();
                return flowData;
            }

            Question q = null;
            for (int i = latestAnswer.getCurrentChildIndex(); i < latestAnswer.getChildren().size(); i++) {
                Question child = latestAnswer.getChildren().get(i);
                if (shouldSkip(child)) {
                    continue;
                }
                q = child;
                break;
            }

            if (q != null) {
                int i = latestAnswer.getQuestionReference().getIndexOfChild(q);
                if (i >= 0) {
                    latestAnswer.setCurrentChildIndex(i);
                } else {
                    throw new IllegalArgumentException("Child not found. Child: " + q.toString());
                }
                flowData.question = q;
                return flowData;
            }

        } catch (Exception e) {
            Timber.e(e.getMessage());
        }

        return flowData;
    }

    private void removeAnswerOfQuestionFromTree(Question toRemove) {
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

    @VisibleForTesting
    public void addDummyAnswersToChildren(Question node) {
        addAnswer(Question.dummyOptions(), node);

        for (Question child : node.getLatestAnswer().getChildren()) {
            addDummyAnswersToChildren(child);
        }
    }

    @VisibleForTesting
    public void addAnswer(ArrayList<Option> response, Question question) {
        if (Question.isLatestAnswerDummy(question)) return; // return if the latest answer is dummy

        if (shouldSkipBasedOnAnswerScope(question))
            return;

        Answer answer = new Answer(response, question, System.currentTimeMillis());
        question.setAnswer(answer);

        Timber.i("Dummy answer created info :\n" + answer.toString());
    }

    private void addEntryToBackStack(FlowData flowData) {
        if (flowData.question == null) {
            return;
        }

        if (flowData.question.getFlowPattern().getQuestionFlow().getUiMode() != QuestionFlow.UI.NONE)
            backStack.addQuestionToStack(flowData);
    }

    /* skip when
        + AnswerData scope demands
        + skip pattern matches
    */
    public static boolean shouldSkip(Question question) {
//        return shouldSkipBasedOnAnswerScope(question) ||
//                shouldSkipBasedOnSkipPattern(question) ||
//                question.isFinished();
        return shouldSkipBasedOnSkipPattern(question);
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

            Question foundForSkipPattern = question.findQuestionUpwards(preFlowQuestionNumber, true);

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
        AnswerFlow answerFlow = question.getFlowPattern().getAnswerFlow();

        if (answerFlow == null) {
            return false;
        }

        AnswerFlow.Modes answerMode = answerFlow.getMode();

        List<Answer> answerList = new ArrayList<>();

        // remove the dummy answers
        for (Answer answer : question.getAnswers()) {
            if (Answer.isAnswerDummy(answer)) continue;
            answerList.add(answer);
        }

        if (answerMode == AnswerFlow.Modes.ONCE) {
            shouldSkip = answerList.size() == 1;
        } else if (answerMode == AnswerFlow.Modes.OPTION) {
            shouldSkip = question.getOptionList().size() == answerList.size();
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

    private static class BackStack {
        private final ArrayList<FlowData> questionStack;

        public BackStack() {
            questionStack = new ArrayList<>();
        }

        public void addQuestionToStack(FlowData flowData) {
            questionStack.add(flowData.copy());
        }

        public boolean isStackEmpty() {
            return questionStack.isEmpty();
        }

        public FlowData getLatest() {
            if (questionStack.isEmpty()) return null;
            return questionStack.get(questionStack.size() - 1);
        }

        public FlowData removeLatest() {
            if (questionStack.isEmpty()) {
                return null;
            }

            int indexToRemove = questionStack.size() - 1;

            // always maintain min of 1 element in the array
            // this is for the ROOT question.
            if (indexToRemove > 0) {
                return questionStack.remove(indexToRemove);
            }

            return null;
        }
    }
}
