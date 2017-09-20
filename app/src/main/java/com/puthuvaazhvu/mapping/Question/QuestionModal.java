package com.puthuvaazhvu.mapping.Question;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Modals.Question;
import com.puthuvaazhvu.mapping.Options.Modal.OptionData;

import java.io.Serializable;
import java.util.ArrayList;

import static com.puthuvaazhvu.mapping.Constants.APIDataConstants.MULTIPLE_ITERATION;
import static com.puthuvaazhvu.mapping.Constants.APIDataConstants.SINGLE_ITERATION;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class QuestionModal implements Parcelable, Serializable {
    String questionID;
    String iterationID;
    String text;
    String rawNumber;
    ArrayList<String> tags;
    ArrayList<OptionData> optionDataList;
    ArrayList<QuestionModal> children;
    QUESTION_TYPE questionType;
    boolean isNextPresent;
    boolean isPreviousPresent;
    Info info;

    public QuestionModal(String questionID
            , String rawNumber
            , String text
            , ArrayList<OptionData> optionDataList
            , QUESTION_TYPE questionType
            , ArrayList<QuestionModal> children
            , ArrayList<String> tags
            , boolean isNextPresent
            , boolean isPreviousPresent
            , Info info) {
        this.questionID = questionID;
        this.text = text;
        this.optionDataList = optionDataList;
        this.questionType = questionType;
        this.isNextPresent = isNextPresent;
        this.isPreviousPresent = isPreviousPresent;
        this.children = children;
        this.tags = tags;
        this.rawNumber = rawNumber;
        this.info = info;
    }

    protected QuestionModal(Parcel in) {
        questionID = in.readString();
        text = in.readString();
        rawNumber = in.readString();
        tags = in.createStringArrayList();
        optionDataList = in.createTypedArrayList(OptionData.CREATOR);
        children = in.createTypedArrayList(QuestionModal.CREATOR);
        isNextPresent = in.readByte() != 0;
        isPreviousPresent = in.readByte() != 0;
        info = in.readParcelable(Info.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(questionID);
        dest.writeString(text);
        dest.writeString(rawNumber);
        dest.writeStringList(tags);
        dest.writeTypedList(optionDataList);
        dest.writeTypedList(children);
        dest.writeByte((byte) (isNextPresent ? 1 : 0));
        dest.writeByte((byte) (isPreviousPresent ? 1 : 0));
        dest.writeParcelable(info, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuestionModal> CREATOR = new Creator<QuestionModal>() {
        @Override
        public QuestionModal createFromParcel(Parcel in) {
            return new QuestionModal(in);
        }

        @Override
        public QuestionModal[] newArray(int size) {
            return new QuestionModal[size];
        }
    };

    public void setIterationID(String iterationID) {
        this.iterationID = iterationID;
    }

    public Info getInfo() {
        return info;
    }

    public String getRawNumber() {
        return rawNumber;
    }

    public ArrayList<String> getTags() {
        return tags;
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

    public QUESTION_TYPE getQuestionType() {
        return questionType;
    }

    public boolean isNextPresent() {
        return isNextPresent;
    }

    public boolean isPreviousPresent() {
        return isPreviousPresent;
    }

    public boolean hasInput() {
        return (questionType == QUESTION_TYPE.INPUT_GPS || questionType == QUESTION_TYPE.INPUT_KEYBOARD);
    }

    public String getTag(String tag) {
        for (String t : tags) {
            if (t.equals(tag)) {
                return t;
            }
        }
        return null;
    }

    public String getIterationTag() {
        for (String t : tags) {
            if (t.equals(MULTIPLE_ITERATION)) {
                return MULTIPLE_ITERATION;
            }
        }
        return SINGLE_ITERATION;
    }

    public void setOther(QuestionModal questionModal) {
        this.questionID = questionModal.getQuestionID();
        this.text = questionModal.getText();
        this.children = questionModal.getChildren();
        this.optionDataList = questionModal.getOptionDataList();
        this.questionType = questionModal.getQuestionType();
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
