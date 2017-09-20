package com.puthuvaazhvu.mapping.utils;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Modals.Option;
import com.puthuvaazhvu.mapping.Modals.Question;
import com.puthuvaazhvu.mapping.Question.QUESTION_TYPE;
import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.util.ArrayList;

import static com.puthuvaazhvu.mapping.Constants.APIDataConstants.BINARY;
import static com.puthuvaazhvu.mapping.Constants.APIDataConstants.INPUT;
import static com.puthuvaazhvu.mapping.Constants.APIDataConstants.LOOP;
import static com.puthuvaazhvu.mapping.Constants.APIDataConstants.MULTIPLE_CHOICE;
import static com.puthuvaazhvu.mapping.Constants.APIDataConstants.SINGLE_CHOICE;
import static com.puthuvaazhvu.mapping.Constants.APIDataConstants.TAG_GPS;

public class ModalAdapters {

    public static QuestionModal getAsQuestionModal(Question question, boolean isTamil) {
        ArrayList<Question> children = question.getChildren();
        ArrayList<QuestionModal> childrenConverted = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            childrenConverted.add(getAsQuestionModal(children.get(i), isTamil));
        }
        return new QuestionModal(question.getId()
                , question.getRawNumber()
                , isTamil ? question.getText().getTamil() : question.getText().getEnglish()
                , getAsOptionDataList(question.getOptionList(), isTamil)
                , getQuestionTypeAsEnum(question.getType(), question.getTags())
                , childrenConverted
                , question.getTags()
                , true
                , true
                , question.getInfo() != null ? QuestionModal.Info.adapter(question.getInfo()) : null); // TODO: look at this. NEXT/BACK button should not be always visible.
    }

    public static ArrayList<OptionData> getAsOptionDataList(ArrayList<Option> options, boolean isTamil) {
        ArrayList<OptionData> result = new ArrayList<>();
        for (Option o : options) {
            result.add(getAsOptionData(o, isTamil));
        }
        return result;
    }

    public static OptionData getAsOptionData(Option option, boolean isTamil) {
        return new OptionData(option.getPosition()
                , false
                , isTamil ? option.getText().getTamil() : option.getText().getEnglish()
                , option.getId()
                , false);
    }

    public static QUESTION_TYPE getQuestionTypeAsEnum(String type, ArrayList<String> tags) {
        if (type.equals(INPUT)) {
            if (isTagPresent(TAG_GPS, tags)) {
                return QUESTION_TYPE.INPUT_GPS;
            } else {
                return QUESTION_TYPE.INPUT_KEYBOARD;
            }
        } else if (type.equals(LOOP)) {
            return QUESTION_TYPE.LOOP;
        } else if (type.equals(MULTIPLE_CHOICE)) {
            return QUESTION_TYPE.MULTIPLE_CHOICE;
        } else if (type.equals(BINARY) || type.equals(SINGLE_CHOICE)) {
            return QUESTION_TYPE.SINGLE_CHOICE;
        }
        return QUESTION_TYPE.NONE;
    }

    public static boolean isTagPresent(String tag, ArrayList<String> tags) {
        for (String t : tags) {
            if (t.equals(tag)) {
                return true;
            }
        }
        return false;
    }
}