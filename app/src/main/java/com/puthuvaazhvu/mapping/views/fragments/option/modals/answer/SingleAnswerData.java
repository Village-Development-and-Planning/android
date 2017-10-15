package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */
public class SingleAnswerData extends AnswerData {
    protected final String optionID;
    protected final String text;
    protected final String position;

    public SingleAnswerData(String questionID, String questionText, String optionID, String text, String position) {
        super(questionID, questionText);
        this.optionID = optionID;
        this.text = text;
        this.position = position;
    }

    public String getOptionID() {
        return optionID;
    }

    public String getText() {
        return text;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public ArrayList<Option> getOption() {
        ArrayList<Option> options = new ArrayList<>();
        Option option = new Option(optionID
                , Types.SINGLE
                , new Text(null, text, text, null)
                , null
                , position);
        options.add(option);
        return options;
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
        dest.writeString(this.position);
    }

    protected SingleAnswerData(Parcel in) {
        super(in);
        this.optionID = in.readString();
        this.text = in.readString();
        this.position = in.readString();
    }

    public static final Creator<SingleAnswerData> CREATOR = new Creator<SingleAnswerData>() {
        @Override
        public SingleAnswerData createFromParcel(Parcel source) {
            return new SingleAnswerData(source);
        }

        @Override
        public SingleAnswerData[] newArray(int size) {
            return new SingleAnswerData[size];
        }
    };
}
