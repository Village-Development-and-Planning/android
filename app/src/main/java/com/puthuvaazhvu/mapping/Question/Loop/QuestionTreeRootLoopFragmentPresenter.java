package com.puthuvaazhvu.mapping.Question.Loop;

import com.puthuvaazhvu.mapping.Survey.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public class QuestionTreeRootLoopFragmentPresenter {
    HashMap<String, OptionData> answeredOptionsMap = new HashMap<>();
    HashMap<String, QuestionModal> answeredQuestionsMap = new HashMap<>();

    public QuestionTreeRootLoopFragmentPresenter() {
    }

    public void insertOptionDataToMap(ArrayList<OptionData> optionData) {
        for (OptionData od : optionData) {
            answeredOptionsMap.put(od.getId(), od);
        }
    }

    public OptionData getOptionsDataFromMap(String optionsID) {
        return answeredOptionsMap.get(optionsID);
    }

    public boolean checkIfAllOptionsHaveBoonAnswered(ArrayList<OptionData> optionData) {
        boolean hasBeenAnswered = false;
        for (OptionData od : optionData) {
            hasBeenAnswered = answeredOptionsMap.containsKey(od.getId());

            if (!hasBeenAnswered) {
                break;
            }
        }
        return hasBeenAnswered;
    }

    public void alterOptionsToDone(QuestionModal root) {
        for (OptionData od : root.getOptionDataList()) {
            if (answeredOptionsMap.containsKey(od.getId())) {
                od.setOptionDone(true);
            }
        }
    }

    public void insertQuestionToMap(QuestionModal questionModal, String optionID) {
        answeredQuestionsMap.put(optionID, questionModal);
    }

    public HashMap<String, HashMap<String, QuestionModal>> getOutputMap(String questionID) {
        HashMap<String, HashMap<String, QuestionModal>> result = new HashMap<>();
        result.put(questionID, answeredQuestionsMap);
        return result;
    }
}
