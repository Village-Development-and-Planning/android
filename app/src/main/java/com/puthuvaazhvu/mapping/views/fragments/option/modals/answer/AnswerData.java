package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public abstract class AnswerData implements Parcelable {
    private final String questionID;
    private final String questionText;

    public AnswerData(String questionID, String questionText) {
        this.questionID = questionID;
        this.questionText = questionText;
    }

    public abstract ArrayList<Option> getOption();

    public String getQuestionID() {
        return questionID;
    }

    public String getQuestionText() {
        return questionText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.questionID);
        dest.writeString(this.questionText);
    }

    protected AnswerData(Parcel in) {
        this.questionID = in.readString();
        this.questionText = in.readString();
    }
}
