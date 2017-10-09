package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;

import com.google.gson.JsonObject;

/**
 * Created by muthuveerappans on 9/30/17.
 */

/*
    Json data of the form:
    {
        id: <val>,
        type: <val>,
        data: {
            text: <val>
        }
    }
 */
public class SingleAnswer extends Answer {
    protected final String optionID;
    protected final String text;

    public SingleAnswer(String questionID, String questionText, String optionID, String text) {
        super(questionID, questionText);
        this.optionID = optionID;
        this.text = text;
    }

    public String getOptionID() {
        return optionID;
    }

    public String getText() {
        return text;
    }

    @Override
    public SelectedOption getSelectedOptions() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", optionID);
        jsonObject.addProperty("type", Types.SINGLE);
        JsonObject data = new JsonObject();
        jsonObject.add("data", data);
        data.addProperty("text", text);
        return new SelectedOption(jsonObject.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.optionID);
        dest.writeString(this.text);
    }

    protected SingleAnswer(Parcel in) {
        super(in);
        this.optionID = in.readString();
        this.text = in.readString();
    }

    public static final Creator<SingleAnswer> CREATOR = new Creator<SingleAnswer>() {
        @Override
        public SingleAnswer createFromParcel(Parcel source) {
            return new SingleAnswer(source);
        }

        @Override
        public SingleAnswer[] newArray(int size) {
            return new SingleAnswer[size];
        }
    };
}
