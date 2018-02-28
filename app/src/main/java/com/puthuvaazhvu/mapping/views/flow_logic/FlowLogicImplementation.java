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
    public FlowData finishCurrentAndGetNext() {
        Question parent = currentQuestion.getParentAnswer().getParentQuestion();

        currentQuestion.getCurrentAnswer().setExitTimestamp(System.currentTimeMillis());

        if (parent == null) return null;
        int indexOfNextQuestion = QuestionUtils.getIndexOfChild(parent, currentQuestion) + 1;
        return _getNext(parent, indexOfNextQuestion, true);
    }

    @Override
    public FlowData moveToIndexInChild(int index) {
        Answer currentAnswer = currentQuestion.getCurrentAnswer();
        if (currentAnswer == null) {
            throw new IllegalArgumentException("The current answer for the question "
                    + currentQuestion.getNumber() + " is null.");
        }

        Question nextQuestion = currentAnswer.getChildren().get(index);
        boolean getChild = false;

        if (!answerFlow(nextQuestion, Answer.createDummyAnswer(nextQuestion))) {
            nextQuestion = currentQuestion;
            getChild = true;
            index++;
        }

        return _getNext(nextQuestion, index, getChild);
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
        return _getNext(currentQuestion, 0, true);
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
            Question visibleQuestion = currentlyVisibleQuestion.getQuestion();
            Answer currentAnswer = visibleQuestion.getCurrentAnswer();
            visibleQuestion.getAnswers().remove(currentAnswer);
            if (visibleQuestion.getAnswers().isEmpty()) {
                visibleQuestion.setCurrentAnswer(null);
            } else {
                visibleQuestion.setCurrentAnswer(visibleQuestion.getAnswers()
                        .get(visibleQuestion.getAnswers().size() - 1));
            }

            Timber.i("------");
            Timber.i("Question popped " + currentlyVisibleQuestion.getQuestion().getNumber());
            Timber.i("Answer count after popping question "
                    + currentlyVisibleQuestion.getQuestion().getNumber() + " "
                    + currentlyVisibleQuestion.getQuestion().getAnswers().size());

            setCurrent(toBeVisibleQuestion.getQuestion());

            Timber.i("Showing question " + toBeVisibleQuestion.getQuestion().getNumber());
            Timber.i("Answer count " + toBeVisibleQuestion.getQuestion().getAnswers().size());
            Timber.i("Question child count " + toBeVisibleQuestion.getQuestion().getChildren().size());
            Timber.i("------");

            return toBeVisibleQuestion;
        }
    }

    // get next algorithm
    // 1. look at the child flow
    // 2. If no question found, move to exit flow
    // 3. After a question is found, add dummy answer based on the answer flow
    // 4. Get the UI fragment
    // 5. Add to back stack
    private FlowData _getNext(Question question, int startingChildIndex, boolean getChild) {
        FlowData flowData = null;
        Question current = question;
        Fragment fragment = null;
        int currentChildIndex = startingChildIndex;

        if (getChild) {

            do {

                // if the answer is empty then shown this question
                if (current.getAnswers().isEmpty()) {
                    break;
                }

                // CHILD FLOW OPERATION

                // if current question child flow is grid, don't bother about the child flow
                // just return this question with the appropriate UI.
                if (QuestionUtils.isGridSelectQuestion(current)) {
                    fragment = new GridQuestionsFragment();
                    flowData = new FlowData();
                    flowData.setQuestion(current);
                    flowData.setFragment(fragment);
                    setCurrent(current);
                    return flowData;
                }

                Answer currentAnswer = current.getCurrentAnswer();
                if (currentAnswer == null) {
                    throw new IllegalArgumentException("The current answer for the question "
                            + current.getNumber() + " is null.");
                }

                Question nq = null;

                for (int i = currentChildIndex; i < currentAnswer.getChildren().size(); i++) {
                    Question child = currentAnswer.getChildren().get(i);
                    if (SkipHelper.shouldSkip(child)) {
                        continue;
                    }
                    nq = child;
                    break;
                }

                if (nq != null) {
                    current = nq;
                    break;
                }

                // EXIT FLOW OPERATION

                current.getCurrentAnswer().setExitTimestamp(System.currentTimeMillis());

                FlowPattern.ExitFlow exitFlow = current.getFlowPattern().getExitFlow();

                if (exitFlow.isIncrementBubble() && !current.getCurrentAnswer().containsOption("0")) {
                    current.setBubbleAnswersCount(current.getBubbleAnswersCount() + 1);
                }

                if (exitFlow.getStrategy() == FlowPattern.ExitFlow.Strategy.END ||
                        current.isRoot()) {
                    backStack.clear();
                    return null;
                } else if (exitFlow.getStrategy() == FlowPattern.ExitFlow.Strategy.LOOP) {

                    FlowPattern.AnswerFlow answerFlow = current.getFlowPattern().getAnswerFlow();

                    if (answerFlow.getMode() == FlowPattern.AnswerFlow.Modes.MULTIPLE) {
                        break;
                    } else if (answerFlow.getMode() == FlowPattern.AnswerFlow.Modes.OPTION) {

                        if (!SkipHelper.shouldSkipBasedOnAnswerScope(current)) {
                            break;
                        }

                    }
                }

                Question parent = current.getParentAnswer().getParentQuestion();

                int indexOfCurrentChild = QuestionUtils.getIndexOfChild(parent, current);
                if (indexOfCurrentChild < 0) {
                    throw new IllegalArgumentException("Question" + current.getNumber() + " not found.");
                }

                currentChildIndex = indexOfCurrentChild + 1;
                current = parent;

            } while (true);

        }

        // PRE FLOW OPERATION
        if (!preFlow(current)) {
            Timber.e("Auth error!");
            // auth error
            return null;
        }

        // ANSWER FLOW OPERATION

        // add dummy answers
        boolean answerFlowOk = false;
        if (QuestionUtils.isLoopOptionsQuestion(current)) {
            if (current.getAnswers().isEmpty()) {
                for (int i = 0; i < current.getOptions().size(); i++) {
                    Option currentOption = current.getOptions().get(i);
                    answerFlowOk = answerFlow(current, Answer.createDummyAnswer(current));
                    if (answerFlowOk) {
                        Option dummy = new Option(currentOption);
                        ArrayList<Option> dummyOptions = new ArrayList<>();
                        dummyOptions.add(dummy);
                        Answer a = current.getCurrentAnswer();
                        a.setLoggedOptions(dummyOptions);
                    }
                }
            } else {
                answerFlowOk = true;
            }
        } else {
            answerFlowOk = answerFlow(current, Answer.createDummyAnswer(current));
        }

        if (!answerFlowOk) {
            // skip the question
            return _getNext(current, 0, true);
        }

        setCurrent(current);

        fragment = getFragment(current);

        // if still UI is null get the next question blindly.
        if (fragment == null) {
            current.getCurrentAnswer().setDummy(false);
            return _getNext(current, 0, true);
        }

        flowData = new FlowData();
        flowData.setFragment(fragment);
        flowData.setQuestion(current);

        // add the found question to back stack
        backStack.addQuestionToStack(flowData);

        return flowData;
    }

    private boolean answerFlow(Question question, Answer answer) {
        FlowPattern flowPattern = question.getFlowPattern();
        if (flowPattern == null) {
            return false;
        }

        FlowPattern.AnswerFlow answerFlow = flowPattern.getAnswerFlow();
        if (answerFlow == null) {
            return false;
        }

        switch (answerFlow.getMode()) {
            case OPTION:
                if (question.getAnswers().size() !=
                        question.getOptions().size()) {
                    question.addAnswer(answer);
                }
                return true;
            case ONCE:
                if (question.getAnswers().size() > 0)
                    question.getAnswers().set(0, answer);
                else
                    question.addAnswer(answer);
                return true;
            case MULTIPLE:
                Answer c = question.getCurrentAnswer();
                if (c == null || !c.isDummy())
                    question.addAnswer(answer);
                return true;
            default:
                return false;
        }
    }

    private boolean preFlow(Question question) {

        if (authJson == null) {
            // authentication error
            Timber.e("Authentication error");
            return false;
        }

        FlowPattern.PreFlow preFlow = question.getFlowPattern().getPreFlow();
        if (preFlow == null) return true;

        // if no pre-flow present in the question, proceed as normal
        ArrayList<String> fill = preFlow.getFill();
        if (fill == null || fill.isEmpty()) return true;

        String surveyorID = SharedPreferenceUtils.getSurveyorID(sharedPreferences);

        if (surveyorID == null) return false;

        JsonObject auth = AuthUtils.getAuthForSurveyCode(authJson, surveyorID);

        if (auth == null) return false;

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

        return true;
    }

    private boolean postFlow(Question question) {
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
                String inputCode = question.getCurrentAnswer().getLoggedOptions().get(0).getTextString();
                JsonObject surveyorAuthJson = AuthUtils.getAuthForSurveyCode(authJson, inputCode);

                SharedPreferenceUtils.putSurveyID(sharedPreferences, inputCode);

                return surveyorAuthJson != null;
            }
        }

        return false;
    }

    private void _update(ArrayList<Option> response, Question question) {
        Timber.i("-----");

        if (QuestionUtils.isLoopOptionsQuestion(question)) {
            // check for a reference answer
            for (int i = 0; i < question.getAnswers().size(); i++) {
                Answer answer = question.getAnswers().get(i);
                if (response.get(0).getPosition()
                        .equals(answer.getLoggedOptions().get(0).getPosition())) {
                    answer.setLoggedOptions(response);
                    answer.setDummy(false);
                    question.setCurrentAnswer(answer);

                    Timber.i("Answer updated info :\n" + question.getAnswers().get(i).toString());

                    return;
                }
            }

            throw new IllegalArgumentException("Answers not properly added for " + question.getNumber());
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

    private Fragment getFragment(Question question) {
        if (question.getFlowPattern() == null ||
                question.getFlowPattern().getQuestionFlow() == null ||
                question.getFlowPattern().getQuestionFlow().getUiMode() == null) {
            return null;
        }

        if (QuestionUtils.isShownTogetherQuestion(question)) {
            return new ShownTogetherFragment();
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
                if (!answer.isDummy() && !answer.getLoggedOptions().isEmpty())
                    loggedOptionPositions.add(answer.getLoggedOptions().get(0).getPosition());
            }

            return Utils.equalLists(originalOptionPositions, loggedOptionPositions);
        }

        private static boolean doesSkipPatternMatchInQuestion(ArrayList<String> optionsSkipPositions, Answer toCheckAnswer) {
            if (optionsSkipPositions == null || toCheckAnswer == null) {
                return false;
            }

            boolean skipPatternMatch = false;
            ArrayList<Option> loggedOptions = toCheckAnswer.getLoggedOptions();
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
