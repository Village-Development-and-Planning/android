package com.puthuvaazhvu.mapping.views.fragments.option.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.AnswerData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class OptionData implements Parcelable {
    public enum Type {
        NONE, CHECKBOX_LIST, RADIO_BUTTON_LIST, BUTTON, EDIT_TEXT, MESSAGE;

        public static Type getType(int ordinal) {
            return values()[ordinal];
        }
    }

    public enum Validation {
        NUMBER, NONE, TEXT
    }

    private final String questionID;
    private final String questionRawNumber;
    private final String questionText;
    private final Type type;
    private final Validation validation;
    private final ArrayList<SingleOptionData> singleOptionData;

    private AnswerData answerData;

    public OptionData(String questionID, String questionRawNumber, String questionText, AnswerData answerData, Type type, Validation validation, ArrayList<SingleOptionData> singleOptionData) {
        this.questionID = questionID;
        this.questionRawNumber = questionRawNumber;
        this.questionText = questionText;
        this.answerData = answerData;
        this.type = type;
        this.validation = validation;
        this.singleOptionData = singleOptionData;
    }

    public OptionData(String questionID, String questionRawNumber, String questionText, Type type, Validation validation, ArrayList<SingleOptionData> singleOptionData) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.type = type;
        this.validation = validation;
        this.singleOptionData = singleOptionData;
        this.questionRawNumber = questionRawNumber;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public AnswerData getAnswerData() {
        return answerData;
    }

    public Type getType() {
        return type;
    }

    public Validation getValidation() {
        return validation;
    }

    public ArrayList<SingleOptionData> getOptions() {
        return singleOptionData;
    }

    public void setAnswerData(AnswerData answerData) {
        this.answerData = answerData;
    }

    public String getQuestionRawNumber() {
        return questionRawNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.questionID);
        dest.writeString(this.questionText);
        dest.writeParcelable(this.answerData, flags);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeInt(this.validation == null ? -1 : this.validation.ordinal());
        dest.writeTypedList(this.singleOptionData);
        dest.writeString(this.questionRawNumber);
    }

    protected OptionData(Parcel in) {
        this.questionID = in.readString();
        this.questionText = in.readString();
        this.answerData = in.readParcelable(AnswerData.class.getClassLoader());
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        int tmpValidation = in.readInt();
        this.validation = tmpValidation == -1 ? null : Validation.values()[tmpValidation];
        this.singleOptionData = in.createTypedArrayList(SingleOptionData.CREATOR);
        this.questionRawNumber = in.readString();
    }

    public static final Creator<OptionData> CREATOR = new Creator<OptionData>() {
        @Override
        public OptionData createFromParcel(Parcel source) {
            return new OptionData(source);
        }

        @Override
        public OptionData[] newArray(int size) {
            return new OptionData[size];
        }
    };

    public static OptionData adapter(Question question) {
        String questionID = question.getRawNumber();
        String questionText = question.getTextString();

        ArrayList<Option> optionsGiven = question.getOptionList();
        ArrayList<SingleOptionData> optionsConverted = null;

        if (optionsGiven != null) {
            optionsConverted = new ArrayList<>(optionsGiven.size());
            for (int i = 0; i < optionsGiven.size(); i++) {
                optionsConverted.add(SingleOptionData.adapter(optionsGiven.get(i), false));
            }
        }

        FlowPattern flowPattern = question.getFlowPattern();

        Type type = null;
        Validation validation = null;

        if (flowPattern != null) {
            type = getTypeFromFlow(flowPattern.getQuestionFlow());
            validation = getValidationFromFlow(question.getFlowPattern().getQuestionFlow());
        }

        return new OptionData(questionID, question.getRawNumber(), questionText, type, validation, optionsConverted);
    }

    public static Validation getValidationFromFlow(QuestionFlow flow) {
        if (flow == null) {
            return Validation.NONE;
        }

        switch (flow.getValidation()) {
            case NUMBER:
                return Validation.NUMBER;
            case TEXT:
                return Validation.TEXT;
            default:
                return Validation.NONE;
        }
    }

    public static Type getTypeFromFlow(QuestionFlow flow) {
        if (flow == null) {
            return Type.NONE;
        }

        switch (flow.getUiMode()) {
            case SINGLE_CHOICE:
                return Type.RADIO_BUTTON_LIST;
            case MULTIPLE_CHOICE:
                return Type.CHECKBOX_LIST;
            case GPS:
                return Type.BUTTON;
            case INPUT:
                return Type.EDIT_TEXT;
            case MESSAGE:
                return Type.MESSAGE;
            default:
                return Type.NONE;
        }
    }
}
