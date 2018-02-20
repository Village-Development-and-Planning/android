package com.puthuvaazhvu.mapping.views.flow_logic;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.application.MappingApplication;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.FlowPattern;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.auth.AuthUtils;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;
import com.puthuvaazhvu.mapping.utils.SharedPreferenceUtils;
import com.puthuvaazhvu.mapping.utils.Utils;
import com.puthuvaazhvu.mapping.views.fragments.question.ConformationQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.GPSQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.GridQuestionsFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.InfoFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.MessageQuestionFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.ShownTogetherFragment;
import com.puthuvaazhvu.mapping.views.fragments.question.SingleQuestionFragment;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 16/01/18.
 */

public class FlowLogicImplementation extends FlowLogic {
    private BackStack backStack;
    private Question currentQuestion;
    private SharedPreferences sharedPreferences;

    public FlowLogicImplementation() {
        super();
        backStack = new BackStack();
    }

    public FlowLogicImplementation(
            Question root,
            SharedPreferences sharedPreferences) {
        this();
        setCurrent(root);
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void setCurrent(Question question) {
        this.currentQuestion = question;
    }

    @Override
    public FlowData getCurrent() {
        FlowData flowData = new FlowData();
        flowData.setFragment(getFragment(currentQuestion));
        flowData.setQuestion(currentQuestion);
        return flowData;
    }

    @Override
    public FlowData finishCurrent() {
        Question parent = currentQuestion.getParentAnswer().getParentQuestion();

        currentQuestion.getCurrentAnswer().setExitTimestamp(System.currentTimeMillis());

        if (parent == null) return null;
        int indexOfNextQuestion = QuestionUtils.getIndexOfChild(parent, currentQuestion) + 1;
        return _getNext(parent, indexOfNextQuestion);
    }

    @Override
    public FlowData moveToIndexInChild(int index) {
        Answer currentAnswer = currentQuestion.getCurrentAnswer();

        if (currentAnswer != null) {
            try {
                Question current = currentAnswer.getChildren().get(index);

                // add a dummy answer
                addDummyAnswer(current);

                FlowData flowData = new FlowData();
                flowData.setFragment(getFragment(current));
                flowData.setQuestion(current);

                // add to back stack
                backStack.addQuestionToStack(flowData);
                // set as the current question
                setCurrent(current);

                return flowData;

            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("The index " + index + " is not present inside the answers. "
                        + currentAnswer.toString());
            }
        } else {
            throw new IllegalArgumentException("The answers list is empty. Check if the current question "
                    + currentQuestion.getNumber() + " is answered first.");
        }
    }

    @Override
    public boolean update(ArrayList<Option> response) {
        _update(response, currentQuestion);

        FlowPattern.PostFlow postFlow = currentQuestion.getFlowPattern().getPostFlow();
        if (postFlow != null) {
            ArrayList<String> postFlowTags = postFlow.getTags();

            if (postFlowTags != null && !postFlowTags.isEmpty()) {
                return postFlow(currentQuestion);
            }
        }

        return true;
    }

    @Override
    public FlowData getNext() {
        return _getNext(currentQuestion, 0);
    }

    @Override
    public FlowData getPrevious() {
        // first remove the last question
        FlowData currentlyVisibleQuestion = backStack.removeLatest();
        if (currentlyVisibleQuestion == null) {
            FlowData flowData = new FlowData();
            flowData.setQuestion(currentQuestion);
            flowData.setFragment(getFragment(currentQuestion));
            return flowData;
        } else {
            FlowData toBeVisibleQuestion = backStack.getLatest();

            // remove the last answer
            currentlyVisibleQuestion.getQuestion().getAnswers()
                    .remove(currentlyVisibleQuestion.getQuestion().getCurrentAnswer());

            Timber.i("------");
            Timber.i("Question popped " + currentlyVisibleQuestion.getQuestion().getNumber());
            Timber.i("Answer count after popping question "
                    + currentlyVisibleQuestion.getQuestion().getAnswers().size());

            setCurrent(toBeVisibleQuestion.getQuestion());

            Timber.i("Showing question " + toBeVisibleQuestion.getQuestion().getNumber());
            Timber.i("Answer count " + toBeVisibleQuestion.getQuestion().getAnswers().size());
            Timber.i("Question child count " + toBeVisibleQuestion.getQuestion().getChildren().size());
            Timber.i("------");

            return toBeVisibleQuestion;
        }
    }

    private FlowData _getNext(Question question, int startingChildIndex) {
        FlowData flowData = null;

        // special case for grid
        if (QuestionUtils.isGridSelectQuestion(question)) {
            flowData = new FlowData();
            flowData.setFragment(new GridQuestionsFragment());
            flowData.setQuestion(question);
        } else {
            Question nextQuestion = getNextQuestion(question, startingChildIndex);

            if (nextQuestion != null) {
                // if loop question, _update logic will handle the addition of answers
                if (!QuestionUtils.isLoopOptionsQuestion(nextQuestion))
                    addDummyAnswer(nextQuestion);

                setCurrent(nextQuestion);

                flowData = new FlowData();
                flowData.setFragment(getFragment(nextQuestion));
                flowData.setQuestion(nextQuestion);

                backStack.addQuestionToStack(flowData);
            }
        }

        // execute the pre flow after the next question is found
        if (flowData != null) {
            preFlow(flowData.getQuestion());
        }

        return flowData;
    }

    private Question getNextQuestion(Question question, int startingChildIndex) {
        Question nextQuestion = childFlow(question, startingChildIndex);
        if (nextQuestion == null) {
            Question exitFlowQuestion = exitFlow(question);
            if (exitFlowQuestion == null) return null;
            if (exitFlowQuestion == question) return exitFlowQuestion;
            int indexOfCurrentChild = QuestionUtils.getIndexOfChild(exitFlowQuestion, question);
            if (indexOfCurrentChild < 0)
                throw new IllegalArgumentException("Question" + question.getNumber() + " not found.");
            int indexOfNextChild = indexOfCurrentChild + 1;
            return getNextQuestion(exitFlowQuestion, indexOfNextChild);
        } else {
            FlowPattern.QuestionFlow nextQuestionQuestionFlow = nextQuestion.getFlowPattern().getQuestionFlow();
            if (nextQuestionQuestionFlow != null &&
                    nextQuestionQuestionFlow.getUiMode() == FlowPattern.QuestionFlow.UI.NONE) {
                addDummyAnswer(nextQuestion);
                nextQuestion = getNextQuestion(nextQuestion, 0);
            }

            return nextQuestion;
        }
    }

    private void preFlow(Question question) {

        if (authJson == null) {
            // authentication error
            Timber.e("Authentication error");
            return;
        }

        FlowPattern.PreFlow preFlow = question.getFlowPattern().getPreFlow();
        if (preFlow == null) return;

        String surveyorID = SharedPreferenceUtils.getSurveyorID(sharedPreferences);

        if (surveyorID == null) return;

        JsonObject auth = AuthUtils.getAuthForSurveyCode(authJson, surveyorID);

        if (auth == null) return;

        ArrayList<String> fill = preFlow.getFill();

        if (fill == null || fill.isEmpty()) return;

        ArrayList<Option> options = new ArrayList<>();

        for (int i = 0; i < fill.size(); i++) {
            JsonElement authFillElement = auth.get(fill.get(i));
            if (authFillElement != null) {
                if (authFillElement.isJsonArray()) {
                    for (int j = 0; j < authFillElement.getAsJsonArray().size(); j++) {
                        JsonElement e = authFillElement.getAsJsonArray().get(j);
                        String value = e.getAsString();
                        options.add(new Option(
                                "",
                                new Text(value, value),
                                "" + j)
                        );
                    }
                } else {
                    String value = authFillElement.getAsString();
                    options.add(new Option(
                            "",
                            new Text(value, value),
                            "" + i)
                    );
                }
            }
        }

        question.getOptions().clear();
        question.getOptions().addAll(options);
    }

    private boolean postFlow(Question question) {
        JsonObject authJson = MappingApplication.globalContext.getApplicationData().getAuthJson();

        if (authJson == null) {
            Timber.e("Auth json is null.");
            return false;
        }

        FlowPattern.PostFlow postFlow = question.getFlowPattern().getPostFlow();
        if (postFlow == null) {
            return false;
        }

        ArrayList<String> postFlowTags = postFlow.getTags();

        if (postFlowTags != null) {
            if (postFlowTags.contains("SURVEYOR_CODE")) {
                String inputCode = question.getCurrentAnswer().getOptions().get(0).getTextString();
                JsonObject surveyorAuthJson = AuthUtils.getAuthForSurveyCode(authJson, inputCode);

                SharedPreferenceUtils.putSurveyID(sharedPreferences, inputCode);

                return surveyorAuthJson != null;
            }
        }

        return false;
    }

    private Question childFlow(Question question, int indexToChild) {
        // if the answer is empty then shown this question
        if (question.getAnswers().isEmpty()) {
            return question;
        }

        Answer currentAnswer = question.getCurrentAnswer();

        Question nextQuestion = null;

        // grid flow
        if (QuestionUtils.isGridSelectQuestion(question) && !currentAnswer.getChildren().isEmpty()) {
            nextQuestion = question;
            return nextQuestion;
        }

        // cascade flow
        int index = 0;
        if (indexToChild > 0) {
            index = indexToChild;
        }

        for (int i = index; i < currentAnswer.getChildren().size(); i++) {
            Question child = currentAnswer.getChildren().get(i);
            if (SkipHelper.shouldSkip(child)) {
                continue;
            }
            nextQuestion = child;
            break;
        }

        return nextQuestion;
    }

    private Question exitFlow(Question question) {
        FlowPattern.ExitFlow exitFlow = question.getFlowPattern().getExitFlow();

        if (exitFlow.isIncrementBubble() && !question.getCurrentAnswer().containsOption("0")) {
            question.setBubbleAnswersCount(question.getBubbleAnswersCount() + 1);
        }

        if (exitFlow.getStrategy() == FlowPattern.ExitFlow.Strategy.END || question.isRoot()) {
            backStack.clear();
            question.getCurrentAnswer().setExitTimestamp(System.currentTimeMillis());
            return null;
        } else if (exitFlow.getStrategy() == FlowPattern.ExitFlow.Strategy.LOOP) {

            FlowPattern.AnswerFlow answerFlow = question.getFlowPattern().getAnswerFlow();

            if (answerFlow.getMode() == FlowPattern.AnswerFlow.Modes.MULTIPLE) {
                return question;
            } else if (answerFlow.getMode() == FlowPattern.AnswerFlow.Modes.OPTION) {
                if (!SkipHelper.shouldSkipBasedOnAnswerScope(question)) {
                    return question;
                }
            }
        }

        question.getCurrentAnswer().setExitTimestamp(System.currentTimeMillis());

        return question.getParentAnswer().getParentQuestion();
    }

    private void _update(ArrayList<Option> response, Question question) {
        Timber.i("-----");

        long startTime = System.currentTimeMillis();

        // if loop question add the answer if not present or _update if answer is already present.
        if (QuestionUtils.isLoopOptionsQuestion(question)) {
            // check for a reference answer
            boolean shouldAddAnswer = true;
            for (int i = 0; i < question.getAnswers().size(); i++) {
                Answer answer = question.getAnswers().get(i);
                if (response.get(0).getPosition().equals(answer.getOptions().get(0).getPosition())) {
                    answer.setLoggedOptions(response);
                    answer.setDummy(false);
                    question.setCurrentAnswer(answer);

                    shouldAddAnswer = false;

                    Timber.i("Answer updated info :\n" + question.getAnswers().get(i).toString());
                }
            }

            if (shouldAddAnswer) {
                Answer answer = new Answer(response, question, startTime);
                answer.setDummy(false);
                question.addAnswer(answer);
            }

            return;
        }

        if (question.getAnswers().size() <= 0) {
            throw new IllegalArgumentException("Answer count for question "
                    + question.getNumber() + " is 0 while updating");
        }

        Answer answer = question.getCurrentAnswer();
        answer.setDummy(false);
        answer.setLoggedOptions(response);

        Timber.i("Answer updated info :\n" + answer.toString());

        Timber.i("-----");
    }

    private void addDummyAnswer(Question question) {
        if (question.getCurrentAnswer() != null && question.getCurrentAnswer().isDummy())
            return; // return if the latest answer is dummy

        Answer dummy = Answer.createDummyAnswer(question);

        setAnswerToQuestionBasedOnType(question, dummy);
    }

    private void setAnswerToQuestionBasedOnType(Question question, Answer answer) {
        FlowPattern flowPattern = question.getFlowPattern();
        if (flowPattern == null || answer.getOptions() == null) {
            question.addAnswer(answer);
            return;
        }

        FlowPattern.AnswerFlow answerFlow = flowPattern.getAnswerFlow();

        if (answerFlow == null) {
            Timber.e("Answer flow null for question " + question.getNumber());
            return;
        }

        if (answerFlow.getMode() == FlowPattern.AnswerFlow.Modes.OPTION) {

            if (question.getAnswers().size() == question.getOptions().size())
                return;

            question.addAnswer(answer);

        } else if (answerFlow.getMode() == FlowPattern.AnswerFlow.Modes.ONCE) {
            if (question.getAnswers().size() > 0)
                question.getAnswers().set(0, answer);
            else question.addAnswer(answer);
        } else {
            question.addAnswer(answer);
        }

        Timber.i("-----");
        Timber.i("Question raw number " + question.getNumber());
        Timber.i("Dummy answer created info :\n" + answer.toString());
        Timber.i("Total answer count for question :\n" + question.getAnswers().size());
        Timber.i("-----");
    }

    private Fragment getFragment(Question question) {
        if (question.getFlowPattern() == null ||
                question.getFlowPattern().getQuestionFlow() == null ||
                question.getFlowPattern().getQuestionFlow().getUiMode() == null) {
            return null;
        }

        if (QuestionUtils.isShownTogetherQuestion(question)) {
            return new ShownTogetherFragment();
        } else if (QuestionUtils.isGridSelectQuestion(question)) {
            return new GridQuestionsFragment();
        } else {
            switch (question.getFlowPattern().getQuestionFlow().getUiMode()) {
                case INFO:
                    return new InfoFragment();
                case CONFIRMATION:
                    return new ConformationQuestionFragment();
                case MESSAGE:
                    return new MessageQuestionFragment();
                case SINGLE_CHOICE:
                case MULTIPLE_CHOICE:
                case INPUT:
                    return new SingleQuestionFragment();
                case GPS:
                    return new GPSQuestionFragment();
                default:
                    return null;
            }
        }
    }

    private static class SkipHelper {
        private static boolean shouldSkip(Question question) {
            return question.isFinished() || shouldSkipBasedOnSkipPattern(question);
        }

        private static boolean shouldSkipBasedOnSkipPattern(Question question) {
            boolean shouldSkip = false;

            // skip pattern
            FlowPattern.PreFlow preFlow = question.getFlowPattern().getPreFlow();

            if (preFlow != null) {
                FlowPattern.PreFlow.SkipUnless skipUnless = preFlow.getSkipUnless();
                if (skipUnless == null) {
                    return false;
                }

                String preFlowQuestionNumber = skipUnless.getQuestionNumber();

                if (preFlowQuestionNumber == null) {
                    return false;
                }

                ArrayList<String> optionsSkip = skipUnless.getSkipPositions();

                Question questionFoundForSkipPattern = QuestionUtils.findQuestionFrom(question,
                        preFlowQuestionNumber, true);

                if (questionFoundForSkipPattern != null && questionFoundForSkipPattern.getCurrentAnswer() != null) {
                    shouldSkip = !doesSkipPatternMatchInQuestion(optionsSkip
                            , questionFoundForSkipPattern.getCurrentAnswer());
                } else {
                    // if no question found then skip
                    shouldSkip = true;
                }
            }

            return shouldSkip;
        }

        private static boolean shouldSkipBasedOnAnswerScope(Question question) {
            boolean shouldSkip = false;
            FlowPattern.AnswerFlow answerFlow = question.getFlowPattern().getAnswerFlow();

            if (answerFlow == null) {
                return false;
            }

            FlowPattern.AnswerFlow.Modes answerMode = answerFlow.getMode();

            List<Answer> answerList = new ArrayList<>();

            // remove the dummy answers
            for (Answer answer : question.getAnswers()) {
                if (answer.isDummy()) continue;
                answerList.add(answer);
            }

            if (answerMode == FlowPattern.AnswerFlow.Modes.ONCE) {
                shouldSkip = answerList.size() == 1;
            } else if (answerMode == FlowPattern.AnswerFlow.Modes.OPTION) {
                shouldSkip = shouldSkipForLoopOptionType(question);
            }

            return shouldSkip;
        }

        private static boolean shouldSkipForLoopOptionType(Question question) {
            ArrayList<Answer> answers = question.getAnswers();

            if (answers.isEmpty() || question.getOptions() == null) return false;

            ArrayList<String> originalOptionPositions = new ArrayList<>();
            ArrayList<String> loggedOptionPositions = new ArrayList<>();

            for (Option option : question.getOptions()) {
                originalOptionPositions.add(option.getPosition());
            }

            for (Answer answer : question.getAnswers()) {
                if (!answer.getOptions().isEmpty())
                    loggedOptionPositions.add(answer.getOptions().get(0).getPosition());
            }

            return Utils.equalLists(originalOptionPositions, loggedOptionPositions);
        }

        private static boolean doesSkipPatternMatchInQuestion(ArrayList<String> optionsSkipPositions, Answer toCheckAnswer) {
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

        public void clear() {
            questionStack.clear();
        }
    }
}
