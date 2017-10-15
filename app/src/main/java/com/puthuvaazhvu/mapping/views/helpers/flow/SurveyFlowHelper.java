package com.puthuvaazhvu.mapping.views.helpers.flow;

import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Flow.AnswerFlow;
import com.puthuvaazhvu.mapping.modals.Flow.ChildFlow;
import com.puthuvaazhvu.mapping.modals.Flow.ExitFlow;
import com.puthuvaazhvu.mapping.modals.Flow.PreFlow;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.utils.DeepCopy.DeepCopy;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.OptionData;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.AnswerData;
import com.puthuvaazhvu.mapping.views.fragments.question.modals.QuestionData;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 10/13/17.
 */

public class SurveyFlowHelper extends FlowHelperBase {
    private static final int STACK_SIZE = 50; // max question count the stack can hold

    private final CircularFifoQueue<Answer> stack; // holds the answered questions (irrespective of copies)


    public SurveyFlowHelper(Question root) {
        super(root);
        stack = new CircularFifoQueue<>(STACK_SIZE);
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

                    Question parent = current.getLatestAnswer().getQuestionReference().getParent();
                    setCurrent(parent);

                } else if (exitFlow.getMode() == ExitFlow.Modes.LOOP) {

                    AnswerFlow answerFlow = current.getFlowPattern().getAnswerFlow();

                    if (answerFlow.getMode() == AnswerFlow.Modes.OPTION) {

                        if (shouldSkipBasedOnAnswerScope(current)) {
                            finishCurrentQuestion();
                        }

                    } else {
                        // return normal flow
                        flowData.flowType = FlowType.SINGLE;
                        flowData.question = current;
                        break;
                    }
                }
            }

        } while (nextQuestion == null);

        return flowData;
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

    @Override
    public FlowHelperBase update(QuestionData questionData) {
        if (!questionData.getSingleQuestion().getId().equals(current.getId())) {
            throw new IllegalArgumentException("ID mismatch " + "current: " + current.getId()
                    + " received: " + questionData.getSingleQuestion().getId());
        }

        OptionData responseData = questionData.getResponseData();
        if (responseData == null) {
            throw new IllegalArgumentException("The response questionData for the given question id "
                    + questionData.getSingleQuestion().getId() + " is null.");
        }

        AnswerData responseAnswerData = responseData.getAnswerData();
        if (responseAnswerData == null) {
            throw new IllegalArgumentException("The response answer for the given question id "
                    + questionData.getSingleQuestion().getId() + " is null.");
        }

        ArrayList<Option> loggedOption = new ArrayList<>();
        loggedOption.addAll(responseAnswerData.getOption());

        Answer answer = new Answer(loggedOption
                , (ArrayList<Question>) DeepCopy.copy(current.getChildren())
                , current);

        ArrayList<Answer> answersLogged = current.getAnswer();
        AnswerFlow answerFlow = current.getFlowPattern().getAnswerFlow();
        if (answerFlow != null && answerFlow.getMode() == AnswerFlow.Modes.ONCE && answersLogged.size() > 0) {
            // already answered
            current.addAnswer(0, answer);
        } else {
            current.addAnswer(answer);
        }

        // add answered question to the stack
        stack.add(answer);

        return this;
    }

    @Override
    public FlowHelperBase moveToIndex(int index) {
        Answer latestAnswer = current.getLatestAnswer();

        if (latestAnswer != null) {
            try {
                setCurrent(latestAnswer.getChildren().get(index));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("The index " + index + " is not present inside the answers.");
            }
        } else {
            throw new IllegalArgumentException("The answers list is empty. Check if the current question "
                    + current.getRawNumber() + "is answered first.");
        }

        return this;
    }

    @Override
    public FlowHelperBase finishCurrentQuestion() {
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
            shouldSkip = question.getAnswer().size() == 1;
        } else if (answerMode == AnswerFlow.Modes.OPTION) {
            shouldSkip = question.getOptionList().size() == question.getAnswer().size();
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
}
