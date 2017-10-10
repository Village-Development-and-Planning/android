package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Option;

import java.util.ArrayList;

import static com.google.gson.JsonNull.INSTANCE;

/**
 * Created by muthuveerappans on 9/30/17.
 */

/*
    Json data of the form:
    {
        id: <val>,
        type: <val>,
        data: [{
            id: <val>,
            data: {
                text: <val>
            }
        }]
    }
 */

public class MultipleAnswer extends Answer {
    private ArrayList<Option> options;

    public MultipleAnswer(String questionID, String questionText, ArrayList<Option> options) {
        super(questionID, questionText);
        this.options = options;
    }

    @Override
    public SelectedOption getSelectedOptions() {
        JsonObject root = new JsonObject();
        root.add("id", INSTANCE);
        root.addProperty("type", Types.MULTIPLE);
        JsonArray jsonArray = new JsonArray();
        for (Option o : options) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", o.getId());
            JsonObject data = new JsonObject();
            data.addProperty("text", o.getText());
            jsonObject.add("data", data);
            jsonArray.add(jsonObject);
        }
        root.add("data", jsonArray);
        return new SelectedOption(root.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.options);
    }

    protected MultipleAnswer(Parcel in) {
        super(in);
        this.options = in.createTypedArrayList(Option.CREATOR);
    }

    public static final Creator<MultipleAnswer> CREATOR = new Creator<MultipleAnswer>() {
        @Override
        public MultipleAnswer createFromParcel(Parcel source) {
            return new MultipleAnswer(source);
        }

        @Override
        public MultipleAnswer[] newArray(int size) {
            return new MultipleAnswer[size];
        }
    };
}
