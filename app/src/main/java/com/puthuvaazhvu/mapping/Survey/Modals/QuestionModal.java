package com.puthuvaazhvu.mapping.Survey.Modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.Modals.Question;
import com.puthuvaazhvu.mapping.Survey.Options.Modal.OptionData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class QuestionModal implements Parcelable, Serializable {
    private String questionID;
    private String iterationID;
    private String text;
    private String rawNumber;
    private ArrayList<OptionData> optionDataList;
    private ArrayList<QuestionModal> children;
    private ArrayList<QuestionType> questionTypes;
    private boolean isNextPresent;
    private boolean isAnswered;
    private boolean isPreviousPresent;
    private Info info;

    public QuestionModal(String questionID
            , String rawNumber
            , String text
            , ArrayList<OptionData> optionDataList
            , ArrayList<QuestionType> questionTypes
            , ArrayList<QuestionModal> children
            , boolean isNextPresent
            , boolean isPreviousPresent
            , boolean isAnswered
            , Info info) {
        this.questionID = questionID;
        this.text = text;
        this.optionDataList = optionDataList;
        this.isNextPresent = isNextPresent;
        this.isPreviousPresent = isPreviousPresent;
        this.children = children;
        this.questionTypes = questionTypes;
        this.rawNumber = rawNumber;
        this.info = info;
        this.isAnswered = isAnswered;
    }


    @Override
    public String toString() {
        return questionID;
    }

    public void setIterationID(String iterationID) {
        this.iterationID = iterationID;
    }

    public Info getInfo() {
        return info;
    }

    public String getRawNumber() {
        return rawNumber;
    }

    public ArrayList<QuestionModal> getChildren() {
        return children;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getIterationID() {
        return iterationID;
    }

    public String getText() {
        return text;
    }

    public ArrayList<OptionData> getOptionDataList() {
        return optionDataList;
    }

    public ArrayList<QuestionType> getQuestionTypes() {
        return questionTypes;
    }

    public boolean hasType(QuestionType questionType) {
        for (QuestionType q : questionTypes) {
            if (q == questionType) {
                return true;
            }
        }
        return false;
    }

    public boolean isNextPresent() {
        return isNextPresent;
    }

    public boolean isPreviousPresent() {
        return isPreviousPresent;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setOther(QuestionModal questionModal) {
        this.questionID = questionModal.getQuestionID();
        this.text = questionModal.getText();
        this.children = questionModal.getChildren();
        this.optionDataList = questionModal.getOptionDataList();
        this.questionTypes = questionModal.getQuestionTypes();
        this.isPreviousPresent = questionModal.isPreviousPresent();
        this.isNextPresent = questionModal.isNextPresent();
        this.isAnswered = questionModal.isAnswered();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.questionID);
        dest.writeString(this.iterationID);
        dest.writeString(this.text);
        dest.writeString(this.rawNumber);
        dest.writeTypedList(this.optionDataList);
        dest.writeTypedList(this.children);
        dest.writeList(this.questionTypes);
        dest.writeByte(this.isNextPresent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAnswered ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPreviousPresent ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.info, flags);
    }

    protected QuestionModal(Parcel in) {
        this.questionID = in.readString();
        this.iterationID = in.readString();
        this.text = in.readString();
        this.rawNumber = in.readString();
        this.optionDataList = in.createTypedArrayList(OptionData.CREATOR);
        this.children = in.createTypedArrayList(QuestionModal.CREATOR);
        this.questionTypes = new ArrayList<QuestionType>();
        in.readList(this.questionTypes, QuestionType.class.getClassLoader());
        this.isNextPresent = in.readByte() != 0;
        this.isAnswered = in.readByte() != 0;
        this.isPreviousPresent = in.readByte() != 0;
        this.info = in.readParcelable(Info.class.getClassLoader());
    }

    public static final Creator<QuestionModal> CREATOR = new Creator<QuestionModal>() {
        @Override
        public QuestionModal createFromParcel(Parcel source) {
            return new QuestionModal(source);
        }

        @Override
        public QuestionModal[] newArray(int size) {
            return new QuestionModal[size];
        }
    };


    public static class Info implements Parcelable, Serializable {
        String questionNumberRaw;
        String option;

        public Info(String questionNumberRaw, String option) {
            this.questionNumberRaw = questionNumberRaw;
            this.option = option;
        }

        public String getQuestionNumberRaw() {
            return questionNumberRaw;
        }

        public String getOption() {
            return option;
        }

        protected Info(Parcel in) {
            questionNumberRaw = in.readString();
            option = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(questionNumberRaw);
            dest.writeString(option);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Info> CREATOR = new Creator<Info>() {
            @Override
            public Info createFromParcel(Parcel in) {
                return new Info(in);
            }

            @Override
            public Info[] newArray(int size) {
                return new Info[size];
            }
        };

        public static Info adapter(Question.Info info) {
            return new Info(info.getQuestionNumberRaw(), info.getOption());
        }
    }
}
