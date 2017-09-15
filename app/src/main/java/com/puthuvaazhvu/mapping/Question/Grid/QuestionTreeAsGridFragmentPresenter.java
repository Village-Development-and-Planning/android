package com.puthuvaazhvu.mapping.Question.Grid;

import android.util.Log;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Modals.Question;
import com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid.GridQuestionModal;
import com.puthuvaazhvu.mapping.Question.QuestionModal;
import com.puthuvaazhvu.mapping.utils.DataHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.puthuvaazhvu.mapping.Constants.APIDataConstants.MULTIPLE_ITERATION;
import static com.puthuvaazhvu.mapping.Constants.APIDataConstants.SINGLE_ITERATION;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public class QuestionTreeAsGridFragmentPresenter {
    ArrayList<GridQuestionModal> gridQuestionModalArrayList;
    HashMap<String, QuestionModal> completedQuestionsMap = new HashMap<>();
    ArrayList<QuestionModal> result = new ArrayList<>();

    public QuestionTreeAsGridFragmentPresenter() {
    }

    public QuestionModal setIDToQuestion(QuestionModal question, String id) {
        question.setIterationID(id);
        return question;
    }

    public ArrayList<GridQuestionModal> convertFrom(ArrayList<QuestionModal> questionModalArrayList) {
        gridQuestionModalArrayList = null;
        gridQuestionModalArrayList = new ArrayList<>();
        for (QuestionModal qm : questionModalArrayList) {
            int questionCount = getCountForQuestion(qm);
            GridQuestionModal gridQuestionModal = GridQuestionModal.questionModalAdapter(qm, questionCount);
            //gridQuestionModal.setQuestionAnswered(completedQuestionsMap.containsKey(qm.getQuestionID()));
            gridQuestionModalArrayList.add(gridQuestionModal);
        }
        return gridQuestionModalArrayList;
    }

    public boolean isIterationAllowed(QuestionModal questionModal) {
        String iterationTAG = questionModal.getIterationTag();
        boolean isAllowed = false;

        if (iterationTAG == null || iterationTAG.equals(SINGLE_ITERATION)) {
            isAllowed = getCountForQuestion(questionModal) < 1;
        } else {
            if (iterationTAG.equals(MULTIPLE_ITERATION)) {
                isAllowed = true;
            }
        }
        return isAllowed;
    }

    public boolean checkIfAllQuestionsAreAnswered(QuestionModal root) {
        int rootCount = root.getChildren().size();
        Set<String> completedQuestionIds = new HashSet<>();

        for (QuestionModal q : root.getChildren()) {
            for (QuestionModal qr : result) {
                if (qr.getQuestionID().equals(q.getQuestionID())) {
                    completedQuestionIds.add(q.getQuestionID());
                    break;
                }
            }
        }

        int resultCount = completedQuestionIds.size();
        return rootCount == resultCount;
    }

    public int getCountForQuestion(QuestionModal questionModal) {
        int count = 0;
        for (QuestionModal q : result) {
            if (questionModal.getQuestionID().equals(q.getQuestionID())) {
                count += 1;
            }
        }
        return count;
    }

    @Deprecated
    public boolean checkIfAllQuestionsAreCompleted(ArrayList<QuestionModal> questionModalArrayList) {
        boolean isCompleted = false;
        for (QuestionModal qm : questionModalArrayList) {
            isCompleted = completedQuestionsMap.containsKey(qm.getQuestionID());

            if (!isCompleted) {

                break;
            }
        }
        return isCompleted;
    }

    public QuestionModal getEntryFromMap(String questionID) {
        return completedQuestionsMap.get(questionID);
    }

    public void insertEntryIntoMap(QuestionModal questionModal) {
        completedQuestionsMap.put(questionModal.getQuestionID(), questionModal);
    }

    public void clearMap() {
        completedQuestionsMap.clear();
    }

    public void addQuestionToResult(QuestionModal questionModal) {
        result.add(questionModal);
    }

    public QuestionModal getQuestionModalFromResult(int i) {
        try {
            return result.get(i);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("The index " + i + " is invalid.");
        }
    }

    public QuestionModal getResult(QuestionModal root) {
        root.getChildren().clear();
        root.getChildren().addAll(result);
        return root;
    }
}
