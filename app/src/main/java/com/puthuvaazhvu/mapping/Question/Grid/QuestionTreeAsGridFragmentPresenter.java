package com.puthuvaazhvu.mapping.Question.Grid;

import com.puthuvaazhvu.mapping.Modals.Question;
import com.puthuvaazhvu.mapping.Question.Grid.RootQuestionsGrid.GridQuestionModal;
import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public class QuestionTreeAsGridFragmentPresenter {
    ArrayList<GridQuestionModal> gridQuestionModalArrayList;
    HashMap<String, QuestionModal> completedQuestionsMap = new HashMap<>();

    public QuestionTreeAsGridFragmentPresenter() {
    }

    public ArrayList<GridQuestionModal> convertFrom(ArrayList<QuestionModal> questionModalArrayList) {
        gridQuestionModalArrayList = null;
        gridQuestionModalArrayList = new ArrayList<>();
        for (QuestionModal qm : questionModalArrayList) {
            GridQuestionModal gridQuestionModal = GridQuestionModal.questionModalAdapter(qm);
            gridQuestionModal.setQuestionAnswered(completedQuestionsMap.containsKey(qm.getQuestionID()));
            gridQuestionModalArrayList.add(gridQuestionModal);
        }
        return gridQuestionModalArrayList;
    }

    public boolean checkIfAllQuestionsAreCompleted(ArrayList<QuestionModal> questionModalArrayList) {
        boolean isCompleted = false;
        for (QuestionModal qm : questionModalArrayList) {
            isCompleted = completedQuestionsMap.containsKey(qm.getQuestionID());
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
}
