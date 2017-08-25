package com.puthuvaazhvu.mapping.Question.Loop;

import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by muthuveerappans on 8/25/17.
 */

public class QuestionTreeRootLoopFragmentPresenter {
    HashMap<String, OptionData> answeredOptionsMap = new HashMap<>();

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
        }
        return hasBeenAnswered;
    }

    public void alterOptionTosDone(QuestionModal root) {
        for (OptionData od : root.getOptionDataList()) {
            if (answeredOptionsMap.containsKey(od.getId())) {
                od.setOptionDone(true);
            }
        }
    }
}
