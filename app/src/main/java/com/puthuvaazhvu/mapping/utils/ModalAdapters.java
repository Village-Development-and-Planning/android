package com.puthuvaazhvu.mapping.utils;

import com.puthuvaazhvu.mapping.Modals.Option;
import com.puthuvaazhvu.mapping.Modals.Question;
import com.puthuvaazhvu.mapping.Question.QUESTION_TYPE;
import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.QuestionModal;

import java.util.ArrayList;

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
                , getQuestionTypeAsEnum(question.getType())
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

    public static QUESTION_TYPE getQuestionTypeAsEnum(String type) {
        if (type.equals("INPUT")) {
            return QUESTION_TYPE.INPUT;
        } else if (type.equals("MAIN_LOOP_DYNAMIC_OPTIONS")) {
            return QUESTION_TYPE.LOOP;
        } else if (type.equals("MULTIPLE_CHOICE")) {
            return QUESTION_TYPE.MULTIPLE;
        } else if (type.equals("BINARY") || type.equals("SINGLE_CHOICE")) {
            return QUESTION_TYPE.SINGLE;
        } else if (type.equals("DETAILS_DYNAMIC_OPTIONS")) {
            return QUESTION_TYPE.DETAILS;
        } else if (type.equals("INPUT_BUTTON")) {
            return QUESTION_TYPE.INPUT_BUTTON;
        }
        return QUESTION_TYPE.NONE;
    }
}