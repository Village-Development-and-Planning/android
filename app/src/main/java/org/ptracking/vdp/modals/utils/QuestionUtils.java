package org.ptracking.vdp.modals.utils;

import org.ptracking.vdp.modals.Answer;
import org.ptracking.vdp.modals.FlowPattern;
import org.ptracking.vdp.modals.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 30/01/18.
 */

public class QuestionUtils {

    public static ArrayList<Integer> getPathOfQuestion(Question question) {
        ArrayList<Integer> indexes = new ArrayList<>();
        getPathOfQuestion(question, indexes);
        Collections.reverse(indexes);
        return indexes;
    }

    // starts from a question ends in root
    private static void getPathOfQuestion(Question node, ArrayList<Integer> indexes) {
        if (node.isRoot()) {
            indexes.add(0); // add index for root
            return; // reached the head of the tree
        }

        int indexOfChild = QuestionUtils.getIndexOfChild(node.getParent(), node);
        indexes.add(indexOfChild);

        Answer parentAnswer = node.getParentAnswer();
        int parentAnswerIndex = parentAnswer.getParentQuestion().getAnswers().indexOf(parentAnswer);
        indexes.add(parentAnswerIndex);

        getPathOfQuestion(parentAnswer.getParentQuestion(), indexes);
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

    public static String getQuestionParentNumber(Question question) {
        if (question.isRoot()) {
            return null;
        }

        int index = question.getNumber().lastIndexOf(".");

        if (index >= 0) {
            return question.getNumber().substring(0, index);
        } else {
            // single digit
            return question.getNumber();
        }
    }

    public static Question findQuestionFrom(Question from, String rawNumberTo, boolean traverseWithAnswers) {
        Question node = findNodeTraversingBack(from, rawNumberTo, traverseWithAnswers);
        if (!node.isRoot() && node.getNumber().equals(rawNumberTo)) {
            return node;
        } else {
            return findNodeTraversingForward(node, rawNumberTo, traverseWithAnswers);
        }
    }

    public static Question findNodeTraversingBack(Question from, String rawNumberTo, boolean traverseWithAnswers) {
        if (from.isRoot()) {
            return from;
        }
        String currentNodeRawNumberWithDot = from.getNumber() + ".";
        String currentNodeRawNumber = from.getNumber();

        if (rawNumberTo.equals(currentNodeRawNumber) || rawNumberTo.contains(currentNodeRawNumberWithDot)) {
            return from;
        } else {
            // move to the parent
            Question parent;
            if (traverseWithAnswers) {
                parent = from.getParentAnswer().getParentQuestion();
            } else {
                parent = from.getParent();
            }
            return findNodeTraversingBack(parent, rawNumberTo, traverseWithAnswers);
        }
    }

    public static Question findNodeTraversingForward(
            Question from,
            String rawNumberTo,
            boolean traverseWithAnswers
    ) {
        String currentNodeRawNumberWithDot;
        String currentNodeRawNumber;

        if (from.isRoot()) {
            currentNodeRawNumber = "";
            currentNodeRawNumberWithDot = "";
        } else {
            currentNodeRawNumberWithDot = from.getNumber() + ".";
            currentNodeRawNumber = from.getNumber();
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

    public static boolean isGridSelectQuestion(Question question) {
        FlowPattern.ChildFlow childFlow = question.getFlowPattern().getChildFlow();

        if (childFlow == null) return false;

        FlowPattern.ChildFlow.Strategy strategy = childFlow.getStrategy();
        FlowPattern.ChildFlow.UI childFlowUI = childFlow.getUiToBeShown();

        return childFlowUI == FlowPattern.ChildFlow.UI.GRID
                && strategy == FlowPattern.ChildFlow.Strategy.SELECT;
    }

    public static boolean isShownTogetherQuestion(Question question) {
        FlowPattern.ChildFlow childFlow = question.getFlowPattern().getChildFlow();

        if (childFlow == null) return false;

        FlowPattern.ChildFlow.Strategy strategy = childFlow.getStrategy();

        return strategy == FlowPattern.ChildFlow.Strategy.TOGETHER;
    }

    public static boolean isLoopOptionsQuestion(Question question) {
        FlowPattern flowPattern = question.getFlowPattern();

        if (flowPattern == null) return false;

        FlowPattern.AnswerFlow answerFlow = flowPattern.getAnswerFlow();

        if (answerFlow == null) return false;

        FlowPattern.ExitFlow exitFlow = flowPattern.getExitFlow();

        if (exitFlow == null) return false;

        return exitFlow.getStrategy() == FlowPattern.ExitFlow.Strategy.LOOP
                && answerFlow.getMode() == FlowPattern.AnswerFlow.Modes.OPTION;
    }

    public static int getIndexOfChild(Question parent, Question child) {
        for (int i = 0; i < parent.getChildren().size(); i++) {
            if (parent.getChildren().get(i).getNumber().equals(child.getNumber())) return i;
        }
        return -1;
    }
}
