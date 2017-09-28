package com.puthuvaazhvu.mapping.Modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

public class Info implements Parcelable {
    private final String questionNumberRaw;
    private final String option;

    public Info(String questionNumberRaw, String option) {
        this.questionNumberRaw = questionNumberRaw;
        this.option = option;
    }

    public Info(JsonObject jsonObject) {
        questionNumberRaw = JsonHelper.getString(jsonObject, "question");
        option = JsonHelper.getString(jsonObject, "option");
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