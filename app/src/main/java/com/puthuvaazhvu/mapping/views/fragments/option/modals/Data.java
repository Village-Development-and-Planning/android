package com.puthuvaazhvu.mapping.views.fragments.option.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.modals.Flow.FlowPattern;
import com.puthuvaazhvu.mapping.modals.Flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.Answer;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class Data implements Parcelable {
    public enum Type {
        NONE, CHECKBOX_LIST, RADIO_BUTTON_LIST, BUTTON, EDIT_TEXT
    }

    public enum Validation {
        NUMBER, NONE, TEXT
    }

    private final String questionID;
    private final String questionText;
    private Answer answer;
    private final Type type;
    private final Validation validation;
    private final ArrayList<Option> options;

    public Data(String questionID, String questionText, Answer answer, Type type, Validation validation, ArrayList<Option> options) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.answer = answer;
        this.type = type;
        this.validation = validation;
        this.options = options;
    }

    public Data(String questionID, String questionText, Type type, Validation validation, ArrayList<Option> options) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.type = type;
        this.validation = validation;
        this.options = options;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public Answer getAnswer() {
        return answer;
    }

    public Type getType() {
        return type;
    }

    public Validation getValidation() {
        return validation;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.questionID);
        dest.writeString(this.questionText);
        dest.writeParcelable(this.answer, flags);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeInt(this.validation == null ? -1 : this.validation.ordinal());
        dest.writeTypedList(this.options);
    }

    protected Data(Parcel in) {
        this.questionID = in.readString();
        this.questionText = in.readString();
        this.answer = in.readParcelable(Answer.class.getClassLoader());
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        int tmpValidation = in.readInt();
        this.validation = tmpValidation == -1 ? null : Validation.values()[tmpValidation];
        this.options = in.createTypedArrayList(Option.CREATOR);
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel source) {
            return new Data(source);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public static Data adapter(Question question) {
        String questionID = question.getId();
        String questionText = question.getTextString();

        ArrayList<com.puthuvaazhvu.mapping.modals.Option> optionsGiven = question.getOptionList();
        ArrayList<Option> optionsConverted = new ArrayList<>(optionsGiven.size());
        for (int i = 0; i < optionsGiven.size(); i++) {
            optionsConverted.add(Option.adapter(optionsGiven.get(i), false));
        }

        Type type = getTypeFromFlow(question.getFlowPattern().getQuestionFlow());
        Validation validation = getValidationFromFlow(question.getFlowPattern().getQuestionFlow());

        return new Data(questionID, questionText, type, validation, optionsConverted);
    }

    public static Validation getValidationFromFlow(QuestionFlow flow) {
        if (flow == null) {
            return Validation.NONE;
        }

        switch (flow.getValidation()) {
            case NUMBER:
                return Validation.NUMBER;
            case SURVEYOR_CODE:
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
            default:
                return Type.NONE;
        }
    }
}
