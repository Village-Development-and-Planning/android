package com.puthuvaazhvu.mapping.utils;

import android.content.Context;
import android.util.Log;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Survey.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Survey.Modals.QuestionModal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class DataHelper {
    /**
     * Helper to update the options of the given question with the latest one. It does using reference.
     *
     * @param singleQuestion The question to update the options.
     *                       This will be updated with the given options by reference.
     * @param optionDataList The new options set.
     */
    public static void updateOptions(QuestionModal singleQuestion, ArrayList<OptionData> optionDataList) {
        ArrayList<OptionData> givenOptionsData = singleQuestion.getOptionDataList();
        for (int i = 0; i < givenOptionsData.size(); i++) {
            OptionData optionData = givenOptionsData.get(i);
            for (OptionData od : optionDataList) {
                if (od.getId().equals(optionData.getId())) {
                    givenOptionsData.set(i, od);
                }
            }
        }
    }

    /**
     * Helper to skip the question based on the pattern provided.
     *
     * @param node The root node in which the child needs to be checked.
     * @param info The info object containing the skip pattern.
     * @return true if the question needs to be shown.
     */
    public static boolean shouldShowQuestion(QuestionModal node, QuestionModal.Info info) {
        if (info == null) {
            return true;
        }

        if (node.getRawNumber().equals(info.getQuestionNumberRaw())) {
            return isOptionSelected(node, info.getOption());
        }

        for (QuestionModal questionModal : node.getChildren()) {
            if (questionModal.getRawNumber().equals(info.getQuestionNumberRaw())) {
                return isOptionSelected(questionModal, info.getOption());
            } else {
                return shouldShowQuestion(questionModal, info);
            }
        }
        return true;
    }

    /**
     * Helper to check if the option is selected or no.
     *
     * @param questionModal
     * @param option
     * @return true if the option is selected.
     */
    public static boolean isOptionSelected(QuestionModal questionModal, String option) {
        ArrayList<OptionData> optionDataArrayList = questionModal.getOptionDataList();
        for (int i = 0; i < optionDataArrayList.size(); i++) {
            if (optionDataArrayList.get(i).getPosition().equals(option)) {
                return optionDataArrayList.get(i).isChecked();
            }
        }
        return false;
    }

    @Deprecated
    public static ArrayList<QuestionModal> convertTreeToList(QuestionModal questionModal) {
        ArrayList<QuestionModal> result = new ArrayList<>();
        result.add(questionModal);

        convertTreeToListInner(questionModal, result);

        return result;
    }

    @Deprecated
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
    @Deprecated
    public static void captureLoggedOptions(QuestionModal root
            , QuestionModal currentQuestion, ArrayList<OptionData> selectedOptions) {

        if (root.equals(currentQuestion)) {
            if (currentQuestion.hasInput()) {
                currentQuestion.getOptionDataList().clear();
                currentQuestion.getOptionDataList().addAll(selectedOptions);
            }
        } else {
            ArrayList<QuestionModal> children = root.getChildren();

            for (QuestionModal qm : children) {
                if (qm.equals(currentQuestion)) {
                    if (currentQuestion.hasInput()) {
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
