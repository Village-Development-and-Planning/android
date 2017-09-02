package com.puthuvaazhvu.mapping.utils;

import android.content.Context;
import android.util.Log;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.QUESTION_TYPE;
import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class DataHelper {
    public static boolean shouldShowQuestion(QuestionModal root, QuestionModal.Info info) {
        if (info == null) {
            return true;
        }

        if (root.getRawNumber().equals(info.getQuestionNumberRaw())) {
            return isOptionChecked(root, info.getOption());
        }

        for (QuestionModal questionModal : root.getChildren()) {
            if (questionModal.getRawNumber().equals(info.getQuestionNumberRaw())) {
                return isOptionChecked(questionModal, info.getOption());
            } else {
                return shouldShowQuestion(questionModal, info);
            }
        }
        return true;
    }

    public static boolean isOptionChecked(QuestionModal questionModal, String option) {
        ArrayList<OptionData> optionDataArrayList = questionModal.getOptionDataList();
        for (int i = 0; i < optionDataArrayList.size(); i++) {
            if (optionDataArrayList.get(i).getText().toLowerCase().equals(option.toLowerCase())) {
                return optionDataArrayList.get(i).isChecked();
            }
        }
        return false;
    }

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

    /**
     * Sets the state of the selected options to boolean true. This automatically updates the root.
     *
     * @param root            The root question to ast as a reference.
     * @param currentQuestion The current question on which the options needs to be modified.
     * @param selectedOptions The selected options.
     */
    public static void captureLoggedOptions(QuestionModal root
            , QuestionModal currentQuestion, ArrayList<OptionData> selectedOptions) {

        if (root.equals(currentQuestion)) {
            if (currentQuestion.getQuestionType() == QUESTION_TYPE.INPUT) {
                currentQuestion.getOptionDataList().clear();
                currentQuestion.getOptionDataList().addAll(selectedOptions);
            }
        } else {
            ArrayList<QuestionModal> children = root.getChildren();

            for (QuestionModal qm : children) {
                if (qm.equals(currentQuestion)) {
                    if (currentQuestion.getQuestionType() == QUESTION_TYPE.INPUT) {
                        currentQuestion.getOptionDataList().clear();
                        currentQuestion.getOptionDataList().addAll(selectedOptions);
                    }
                }
            }
        }
    }

    public static String readFromAssetsFile(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.i(Constants.LOG_TAG, "Error reading the JSON file from assets. " + ex.getMessage());
        }
        return json;
    }
}
