package com.puthuvaazhvu.mapping.Modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Question implements Parcelable {
    String id;
    String position;
    Text text;
    String type;
    ArrayList<Option> optionList;
    ArrayList<String> tags;
    String modifiedAt;
    String rawNumber;
    ArrayList<Question> children;
    Info info;

    public Question(String id
            , String position
            , String rawNumber
            , Text text
            , String type
            , ArrayList<Option> optionList
            , ArrayList<String> tags
            , String modifiedAt
            , ArrayList<Question> children
            , Info info) {
        this.id = id;
        this.position = position;
        this.text = text;
        this.type = type;
        this.optionList = optionList;
        this.tags = tags;
        this.modifiedAt = modifiedAt;
        this.children = children;
        this.rawNumber = rawNumber;
        this.info = info;
    }

    protected Question(Parcel in) {
        id = in.readString();
        position = in.readString();
        text = in.readParcelable(Text.class.getClassLoader());
        type = in.readString();
        optionList = in.createTypedArrayList(Option.CREATOR);
        tags = in.createStringArrayList();
        modifiedAt = in.readString();
        rawNumber = in.readString();
        children = in.createTypedArrayList(Question.CREATOR);
        info = in.readParcelable(Info.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(position);
        dest.writeParcelable(text, flags);
        dest.writeString(type);
        dest.writeTypedList(optionList);
        dest.writeStringList(tags);
        dest.writeString(modifiedAt);
        dest.writeString(rawNumber);
        dest.writeTypedList(children);
        dest.writeParcelable(info, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public String getRawNumber() {
        return rawNumber;
    }

    public String getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }

    public Text getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public ArrayList<Option> getOptionList() {
        return optionList;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public ArrayList<Question> getChildren() {
        return children;
    }

    public Info getInfo() {
        return info;
    }

    public static class Info implements Parcelable {
        String questionNumberRaw;
        String option;

        public Info(String questionNumberRaw, String option) {
            this.questionNumberRaw = questionNumberRaw;
            this.option = option;
        }

        protected Info(Parcel in) {
            questionNumberRaw = in.readString();
            option = in.readString();
        }

        public String getQuestionNumberRaw() {
            return questionNumberRaw;
        }

        public String getOption() {
            return option;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(questionNumberRaw);
            parcel.writeString(option);
        }
    }
}
