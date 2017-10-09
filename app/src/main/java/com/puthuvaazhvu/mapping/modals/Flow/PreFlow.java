package com.puthuvaazhvu.mapping.modals.Flow;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class PreFlow implements Parcelable {
    private final ArrayList<String> fill;
    private final String questionSkipRawNumber;
    private final ArrayList<String> optionSkip;

    public PreFlow(ArrayList<String> fill, String questionSkipRawNumber, ArrayList<String> optionSkip) {
        this.fill = fill;
        this.questionSkipRawNumber = questionSkipRawNumber;
        this.optionSkip = optionSkip;
    }

    public PreFlow(JsonObject jsonObject) {
        JsonArray fillJsonArray = JsonHelper.getJsonArray(jsonObject, "fill");

        if (fillJsonArray != null)
            fill = (JsonHelper.getStringArray(fillJsonArray));
        else fill = null;

        JsonObject skipUnlessJson = JsonHelper.getJsonObject(jsonObject, "skipUnless");
        if (skipUnlessJson != null) {
            questionSkipRawNumber = JsonHelper.getString(skipUnlessJson, "question");
            optionSkip = new ArrayList<>(Arrays.asList(JsonHelper.getString(skipUnlessJson, "option").split(",")));
        } else {
            questionSkipRawNumber = null;
            optionSkip = null;
        }
    }

    public ArrayList<String> getFill() {
        return fill;
    }

    public String getQuestionSkipRawNumber() {
        return questionSkipRawNumber;
    }

    public ArrayList<String> getOptionSkip() {
        return optionSkip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.fill);
        dest.writeString(this.questionSkipRawNumber);
        dest.writeStringList(this.optionSkip);
    }

    protected PreFlow(Parcel in) {
        this.fill = in.createStringArrayList();
        this.questionSkipRawNumber = in.readString();
        this.optionSkip = in.createStringArrayList();
    }

    public static final Creator<PreFlow> CREATOR = new Creator<PreFlow>() {
        @Override
        public PreFlow createFromParcel(Parcel source) {
            return new PreFlow(source);
        }

        @Override
        public PreFlow[] newArray(int size) {
            return new PreFlow[size];
        }
    };
}
