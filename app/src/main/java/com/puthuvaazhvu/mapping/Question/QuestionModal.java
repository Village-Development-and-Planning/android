package com.puthuvaazhvu.mapping.Question;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Options.Modal.OPTION_TYPES;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class QuestionModal implements Parcelable {
    String questionID;
    String text;
    ArrayList<OptionData> optionDataList;
    ArrayList<QuestionModal> children;
    OPTION_TYPES optionType;
    boolean isNextPresent;
    boolean isPreviousPresent;

    public QuestionModal(String questionID, String text, ArrayList<OptionData> optionDataList, OPTION_TYPES optionType, ArrayList<QuestionModal> children, boolean isNextPresent, boolean isPreviousPresent) {
        this.questionID = questionID;
        this.text = text;
        this.optionDataList = optionDataList;
        this.optionType = optionType;
        this.isNextPresent = isNextPresent;
        this.isPreviousPresent = isPreviousPresent;
        this.children = children;
    }

    protected QuestionModal(Parcel in) {
        questionID = in.readString();
        text = in.readString();
        optionDataList = in.createTypedArrayList(OptionData.CREATOR);
        isNextPresent = in.readByte() != 0;
        isPreviousPresent = in.readByte() != 0;
        optionType = OPTION_TYPES.valueOf(in.readString());
        children = in.createTypedArrayList(QuestionModal.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(questionID);
        dest.writeString(text);
        dest.writeTypedList(optionDataList);
        dest.writeByte((byte) (isNextPresent ? 1 : 0));
        dest.writeByte((byte) (isPreviousPresent ? 1 : 0));
        dest.writeString(optionType.name());
        dest.writeTypedList(children);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<QuestionModal> CREATOR = new Parcelable.Creator<QuestionModal>() {
        @Override
        public QuestionModal createFromParcel(Parcel in) {
            return new QuestionModal(in);
        }

        @Override
        public QuestionModal[] newArray(int size) {
            return new QuestionModal[size];
        }
    };

    public ArrayList<QuestionModal> getChildren() {
        return children;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getText() {
        return text;
    }

    public ArrayList<OptionData> getOptionDataList() {
        return optionDataList;
    }

    public OPTION_TYPES getOptionType() {
        return optionType;
    }

    public boolean isNextPresent() {
        return isNextPresent;
    }

    public boolean isPreviousPresent() {
        return isPreviousPresent;
    }

    public void setOther(QuestionModal questionModal) {
        this.questionID = questionModal.getQuestionID();
        this.text = questionModal.getText();
        this.children = questionModal.getChildren();
        this.optionDataList = questionModal.getOptionDataList();
        this.optionType = questionModal.getOptionType();
        this.isPreviousPresent = questionModal.isPreviousPresent();
        this.isNextPresent = questionModal.isNextPresent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionModal that = (QuestionModal) o;

        return questionID != null ? questionID.equals(that.questionID) : that.questionID == null;

    }

    @Override
    public int hashCode() {
        return questionID != null ? questionID.hashCode() : 0;
    }

}
