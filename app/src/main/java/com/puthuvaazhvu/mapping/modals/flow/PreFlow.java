package com.puthuvaazhvu.mapping.modals.flow;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.BaseObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class PreFlow extends BaseObject implements Parcelable {
    private final ArrayList<String> fill;
    private final String questionSkipRawNumber;
    private final ArrayList<String> optionSkip;

    public static class Tag {
        public static final String HABITATION_NAME = "HABITATION_NAME";
        public static final String DISTRICT_NAME = "DISTRICT_NAME";
        public static final String BLOCK_NAME = "BLOCK_NAME";
        public static final String PANCHAYAT_NAME = "PANCHAYAT_NAME";
        public static final String VILLAGE_NAME = "VILLAGE_NAME";
    }

    public PreFlow(ArrayList<String> fill, String questionSkipRawNumber, ArrayList<String> optionSkip) {
        this.fill = fill;
        this.questionSkipRawNumber = questionSkipRawNumber;
        this.optionSkip = optionSkip;
    }

    public PreFlow(JsonObject jsonObject) {
        JsonArray fillJsonArray = JsonHelper.getJsonArray(jsonObject, "fill");

        if (fillJsonArray != null)
            fill = (JsonHelper.getStringArray(fillJsonArray));
        else fill = new ArrayList<>();

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

    @Override
    public JsonElement getAsJson() {
        return null;
    }
}
