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

    public int getCountForQuestion(QuestionModal questionModal) {
        int count = 0;
        for (QuestionModal q : result) {
            if (questionModal.getQuestionID().equals(q.getQuestionID())) {
                count += 1;
            }
        }
        return count;
    }

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
