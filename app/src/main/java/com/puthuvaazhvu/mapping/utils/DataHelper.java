package com.puthuvaazhvu.mapping.utils;

import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Options.OPTION_TYPES;
import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class DataHelper {

    public static ArrayList<QuestionModal> convertTreeToList(QuestionModal questionModal) {
        ArrayList<QuestionModal> result = new ArrayList<>();
        result.add(questionModal);

        convertTreeToListInner(questionModal, result);

        return result;
    }

    private static void convertTreeToListInner(QuestionModal questionModal, ArrayList<QuestionModal> result) {
        ArrayList<QuestionModal> children = questionModal.getChildren();

        for (QuestionModal qm : children) {
            result.add(qm);
            convertTreeToListInner(qm, result);
        }

    }

    public static void captureLoggedOptions(QuestionModal root
            , QuestionModal currentQuestion, ArrayList<OptionData> selectedOptions) {

        ArrayList<QuestionModal> children = root.getChildren();

        for (QuestionModal qm : children) {
            if (qm.equals(currentQuestion)) {
                if (currentQuestion.getOptionType() == OPTION_TYPES.INPUT) {
                    currentQuestion.getOptionDataList().clear();
                    currentQuestion.getOptionDataList().addAll(selectedOptions);
                } else {
                    modifyOptions(qm.getOptionDataList(), selectedOptions);
                }
            }
        }
    }

    public static void modifyOptions(ArrayList<OptionData> given, ArrayList<OptionData> to) {
        for (OptionData od : given) {
            for (OptionData ood : to) {
                if (od.getId().equals(ood.getId())) {
                    od.setChecked(ood.isChecked());
                } else {
                    od.setChecked(false);
                }
            }
        }
    }
}
