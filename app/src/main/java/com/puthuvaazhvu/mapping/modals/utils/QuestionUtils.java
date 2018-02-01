package com.puthuvaazhvu.mapping.modals.utils;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.modals.flow.AnswerFlow;
import com.puthuvaazhvu.mapping.modals.flow.ExitFlow;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;
import com.puthuvaazhvu.mapping.modals.flow.PreFlow;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 30/01/18.
 */

public class QuestionUtils {

    public static String getTextString(Question question) {
        if (question.getText() == null) {
            return null;
        }
        switch (Constants.APP_LANGUAGE) {
            case TAMIL:
                return question.getText().getTamil();
            default:
                return question.getText().getEnglish();
        }
    }

    public static ArrayList<Integer> getPathOfQuestion(Question question) {
        ArrayList<Integer> indexes = new ArrayList<>();
        getPathOfQuestion(question, indexes);
        Collections.reverse(indexes);
        return indexes;
    }

    // starts from a question ends in root
    public static void getPathOfQuestion(Question node, ArrayList<Integer> indexes) {
        if (node.isRoot()) {
            indexes.add(0); // add index for root
            return; // reached the head of the tree
        }

        int indexOfChild = QuestionUtils.getIndexOfChild(node.getParent(), node);
        indexes.add(indexOfChild);

        Answer parentAnswer = node.getParentAnswer();
        int parentAnswerIndex = parentAnswer.getQuestionReference().getAnswers().indexOf(parentAnswer);
        indexes.add(parentAnswerIndex);

        getPathOfQuestion(parentAnswer.getQuestionReference(), indexes);
    }

    public static Question moveToQuestionUsingPath(String snapshotPath, Question root) {
        String[] indexesInString = snapshotPath.split(",");

        ArrayList<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < indexesInString.length; i++) {
            try {
                indexes.add(Integer.parseInt(indexesInString[i]));
            } catch (NumberFormatException e) {
                Timber.e("Error occurred while parsing snapshot path: "
                        + snapshotPath + " error:" + e.getMessage());
            }
        }

        try {

            Question node = root;

            for (int i = 1; i < indexes.size() - 1; i += 2) {
                int answerIndex = indexes.get(i);
                int questionIndex = indexes.get(i + 1);
                node = node.getAnswers().get(answerIndex).getChildren().get(questionIndex);
            }

            return node;

        } catch (Exception e) {
            String err = "Question not properly populated " + e.getLocalizedMessage();
            Timber.e(err);
        }

        return root;
    }

    public static boolean isCurrentAnswerDummy(Question question) {
        if (question.getAnswers().size() <= 0) {
            return false;
        }
        return AnswerUtils.isAnswerDummy(question.getCurrentAnswer());
    }

    public static Question populateAnswersFromJson(final Question node, JsonObject nodeJson) {

        if (nodeJson.has("question"))
            nodeJson = JsonHelper.getJsonObject(nodeJson, "question");

        JsonArray answersJsonArray = JsonHelper.getJsonArray(nodeJson, "answers");

        if (answersJsonArray != null) {

            for (int i = 0; i < answersJsonArray.size(); i++) {

                JsonObject answersJson = answersJsonArray.get(i).getAsJsonObject();
                JsonArray loggedOptionsJsonArray = JsonHelper.getJsonArray(answersJson, "logged_options");

                ArrayList<Option> options = new ArrayList<>();

                if (loggedOptionsJsonArray != null) {

                    for (int j = 0; j < loggedOptionsJsonArray.size(); j++) {
                        options.add(new Option(loggedOptionsJsonArray.get(j).getAsJsonObject()));
                    }

                }

                // create the answer object
                Answer answer = new Answer(options, node);
                node.addAnswer(answer);

                // answers children length and this array length should be the same
                JsonArray childrenJsonArray = JsonHelper.getJsonArray(answersJson, "children");

                if (childrenJsonArray != null) {

                    if (childrenJsonArray.size() != answer.getChildren().size()) {
                        throw new IllegalArgumentException("The length should be the same.");
                    }

                    for (int k = 0; k < childrenJsonArray.size(); k++) {

                        Question child = answer.getChildren().get(k);
                        JsonObject childJson = childrenJsonArray.get(k).getAsJsonObject();

                        populateAnswersFromJson(child, childJson);

                    }
                }
            }
        }

        return node;
    }

    public static String getQuestionParentNumber(Question question) {
        if (question.isRoot()) {
            return null;
        }

        int index = question.getRawNumber().lastIndexOf(".");

        if (index >= 0) {
            return question.getRawNumber().substring(0, index);
        } else {
            // single digit
            return question.getRawNumber();
        }
    }

    /**
     * Recursively populates the node.
     *
     * @param question The root node question. (Null usually, if it's starting node)
     * @param nodeJson The corresponding JSON
     * @return The populated SingleQuestionFragment object.
     */
    public static Question populateQuestionFromJson(Question question, JsonObject nodeJson) {
        if (question == null) // if null, start of the new node.
            question = new Question(nodeJson);

        JsonObject questionJson = JsonHelper.getJsonObject(nodeJson, "question");
        if (questionJson != null) {
            JsonArray childrenArray = JsonHelper.getJsonArray(questionJson, "children");
            if (childrenArray != null) {
                for (JsonElement e : childrenArray) {
                    JsonObject o = e.getAsJsonObject();
                    Question c = new Question(o);
                    question.addChild(c);
                    populateQuestionFromJson(c, o);
                }
            }
        }
        return question;
    }

    public static Question findQuestionFrom(Question from, String rawNumberTo, boolean traverseWithAnswers) {
        Question node = findNodeTraversingBack(from, rawNumberTo, traverseWithAnswers);
        if (!node.isRoot() && node.getRawNumber().equals(rawNumberTo)) {
            return node;
        } else {
            return findNodeTraversingForward(node, rawNumberTo, traverseWithAnswers);
        }
    }

    public static Question findNodeTraversingBack(Question from, String rawNumberTo, boolean traverseWithAnswers) {
        if (from.isRoot()) {
            return from;
        }
        String currentNodeRawNumberWithDot = from.getRawNumber() + ".";
        String currentNodeRawNumber = from.getRawNumber();

        if (rawNumberTo.equals(currentNodeRawNumber) || rawNumberTo.contains(currentNodeRawNumberWithDot)) {
            return from;
        } else {
            // move to the parent
            Question parent;
            if (traverseWithAnswers) {
                parent = from.getParentAnswer().getQuestionReference();
            } else {
                parent = from.getParent();
            }
            return findNodeTraversingBack(parent, rawNumberTo, traverseWithAnswers);
        }
    }

    public static Question findNodeTraversingForward(Question from, String rawNumberTo, boolean traverseWithAnswers) {
        String currentNodeRawNumberWithDot;
        String currentNodeRawNumber;

        if (from.isRoot()) {
            currentNodeRawNumber = "";
            currentNodeRawNumberWithDot = "";
        } else {
            currentNodeRawNumberWithDot = from.getRawNumber() + ".";
            currentNodeRawNumber = from.getRawNumber();
        }

        if (rawNumberTo.equals(currentNodeRawNumber)) {
            return from;
        } else {
            Question result = null;
            if (from.isRoot() || rawNumberTo.contains(currentNodeRawNumberWithDot)) {
                // move to child
                List<Question> children;

                if (!traverseWithAnswers) {
                    children = from.getChildren();
                } else {
                    if (from.getAnswers().isEmpty()) {
                        return null;
                    }
//                    children = QuestionUtils.getLastAnswer(from).getChildren();
                    children = from.getCurrentAnswer().getChildren();
                }

                for (Question c : children) {
                    result = findNodeTraversingForward(c, rawNumberTo, traverseWithAnswers);
                    if (result != null) {
                        break;
                    }
                }
            }
            return result;
        }
    }

    public static int getValidAnswersCount(Question question) {
        int count = 0;
        for (Answer answer : question.getAnswers()) {
            if (!AnswerUtils.isAnswerDummy(answer)) {
                count++;
            }
        }
        return count;
    }

    public static boolean isLoopQuestion(Question question) {
        FlowPattern flowPattern = question.getFlowPattern();

        if (flowPattern == null) return false;

        AnswerFlow answerFlow = flowPattern.getAnswerFlow();

        if (answerFlow == null) return false;

        ExitFlow exitFlow = flowPattern.getExitFlow();

        if (exitFlow == null) return false;

        return exitFlow.getMode() == ExitFlow.Modes.LOOP && answerFlow.getMode() == AnswerFlow.Modes.OPTION;
    }

    public static boolean containsPreFlowTag(Question question, String tag) {
        if (question.getFlowPattern() == null) {
            return false;
        }

        PreFlow preFlow = question.getFlowPattern().getPreFlow();
        if (preFlow != null) {
            ArrayList<String> fillTags = preFlow.getFill();
            if (fillTags != null) {
                for (String s : fillTags) {
                    if (tag.toLowerCase().equals(s.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getIndexOfChild(Question parent, Question child) {
        for (int i = 0; i < parent.getChildren().size(); i++) {
            if (parent.getChildren().get(i).getRawNumber().equals(child.getRawNumber())) return i;
        }
        return -1;
    }

    public interface QuestionTreeSearchPredicate {
        boolean evaluate(Question question);
    }

    public static Question findQuestionInTreeWithPredicate(Question questionFrom, QuestionTreeSearchPredicate predicate) {
        if (predicate.evaluate(questionFrom)) {
            return questionFrom;
        }
        Question result = null;
        for (Question child : questionFrom.getChildren()) {

            result = findQuestionInTreeWithPredicate(child, predicate);

            if (result != null) {
                break;
            }

        }
        return result;
    }

    public synchronized static JsonObject convertToJson(Question node) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("position", node.getPosition());

        JsonObject questionJson = new JsonObject();
        jsonObject.add("question", questionJson);

        if (node.getFlowPattern() != null)
            questionJson.add("flow", node.getFlowPattern().getAsJson());

        questionJson.addProperty("id", "");
        //jsonObject.addProperty("position", node.getPosition());

        if (node.getText() != null)
            questionJson.add("text", node.getText().getAsJson());

        questionJson.addProperty("type", node.getType());
        questionJson.addProperty("number", node.getRawNumber());

        // options
        JsonArray optionsArray = new JsonArray();

        if (node.getOptionList() != null)
            for (Option o : node.getOptionList()) {
                optionsArray.add(o.getAsJson());
            }

        questionJson.add("options", optionsArray);

        // answers
        JsonArray answersArray = new JsonArray();

        if (node.getAnswers() != null)
            for (Answer a : node.getAnswers()) {
                answersArray.add(a.getAsJson());
            }

        questionJson.add("answers", answersArray);

        // children
        JsonArray childrenArray = new JsonArray();

        if (node.getChildren() != null)
            for (Question c : node.getChildren()) {
                childrenArray.add(convertToJson(c));
            }

        questionJson.add("children", childrenArray);

        return jsonObject;
    }

    public static void removeAnswerFromQuestion(Question question) {
        AnswerFlow answerFlow = question.getFlowPattern().getAnswerFlow();

        if (answerFlow == null) {
            return;
        }

        if (answerFlow.getMode() == AnswerFlow.Modes.ONCE) {
            question.getAnswers().clear();
        }

        // for OPTION the answers are going to be updated anyway
        // we don't care for MULTIPLE
    }

    public static ArrayList<Option> generateQuestionWithDummyAndValidOptions() {
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("", "NO DATA",
                new Text("", "", "", ""),
                "", ""));
        return options;
    }

    public static ArrayList<Option> generateQuestionWithDummyOptions() {
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("DUMMY", "DUMMY",
                new Text("DUMMY", "dummy", "dummy", ""),
                "", ""));
        return options;
    }

    public static Question fillDynamicOptionsForQuestion(Question question, ArrayList<Option> options) {
        question.getOptionList().clear();
        question.getOptionList().addAll(options);
        return question;
    }
}
