package com.puthuvaazhvu.mapping.views.fragments.option.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.views.fragments.option.modals.answer.Answer;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class Data implements Parcelable {
    public enum Type {
        NONE, CHECKBOX_LIST, RADIO_BUTTON_LIST, BUTTON, EDIT_TEXT
    }

    private String questionID;
    private String questionText;
    private Answer answer;
    private Type type;
    private ArrayList<Option> options;

    public Data(String questionID, String questionText, Answer answer, Type type, ArrayList<Option> options) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.answer = answer;
        this.type = type;
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

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Type getType() {
        return type;
    }

    public ArrayList<Option> getOptions() {
        return options;
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
        dest.writeTypedList(this.options);
    }

    protected Data(Parcel in) {
        this.questionID = in.readString();
        this.questionText = in.readString();
        this.answer = in.readParcelable(Answer.class.getClassLoader());
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
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
}
