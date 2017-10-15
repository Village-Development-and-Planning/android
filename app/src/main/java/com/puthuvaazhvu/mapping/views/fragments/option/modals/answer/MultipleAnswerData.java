package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Text;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.SingleOptionData;

import java.util.ArrayList;

import static com.google.gson.JsonNull.INSTANCE;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class MultipleAnswerData extends AnswerData {
    private ArrayList<SingleOptionData> singleOptionData;

    public MultipleAnswerData(String questionID, String questionText, ArrayList<SingleOptionData> singleOptionData) {
        super(questionID, questionText);
        this.singleOptionData = singleOptionData;
    }

    @Override
    public ArrayList<Option> getOption() {
        ArrayList<Option> options = new ArrayList<>();
        for (SingleOptionData sod : singleOptionData) {
            Option option = new Option(sod.getId()
                    , Types.MULTIPLE
                    , new Text(null, sod.getText(), sod.getText(), null)
                    , null
                    , sod.getPosition());
            options.add(option);
        }
        return options;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.singleOptionData);
    }

    protected MultipleAnswerData(Parcel in) {
        super(in);
        this.singleOptionData = in.createTypedArrayList(SingleOptionData.CREATOR);
    }

    public static final Creator<MultipleAnswerData> CREATOR = new Creator<MultipleAnswerData>() {
        @Override
        public MultipleAnswerData createFromParcel(Parcel source) {
            return new MultipleAnswerData(source);
        }

        @Override
        public MultipleAnswerData[] newArray(int size) {
            return new MultipleAnswerData[size];
        }
    };
}
