package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

/**
 * Created by muthuveerappans on 9/30/17.
 */

/*
    Base template for all the types of answers
    Json data of the form:
    {
        id: <val>,
        type: <val>,
        data: {}
    }
*/
public abstract class Answer implements Parcelable {
    private final String questionID;
    private final String questionText;
    private JsonParser jsonParser;

    public Answer(String questionID, String questionText) {
        this.questionID = questionID;
        this.questionText = questionText;
        jsonParser = new JsonParser();
    }

    public abstract SelectedOption getSelectedOptions();

    public String getQuestionID() {
        return questionID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOptionID() {
        JsonObject jsonObject = jsonParser.parse(getSelectedOptions().getJson()).getAsJsonObject();
        return JsonHelper.getString(jsonObject, "id");
    }

    public String getOptionType() {
        JsonObject jsonObject = jsonParser.parse(getSelectedOptions().getJson()).getAsJsonObject();
        return JsonHelper.getString(jsonObject, "type");
    }

    public JsonElement getOptionData() {
        JsonObject jsonObject = jsonParser.parse(getSelectedOptions().getJson()).getAsJsonObject();
        return jsonObject.get("data");
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

    protected Answer(Parcel in) {
        this.questionID = in.readString();
        this.questionText = in.readString();
    }
}
