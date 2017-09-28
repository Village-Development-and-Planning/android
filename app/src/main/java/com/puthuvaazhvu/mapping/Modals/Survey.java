package com.puthuvaazhvu.mapping.Modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Survey implements Parcelable {
    private final String id;
    private final String name;
    private final String description;
    private final ArrayList<Question> questionList;
    private final String modifiedAt;

    public Survey(String id, String name, String description, ArrayList<Question> questionList, String modifiedAt) {
        this.id = id;
        this.name = name;
        this.questionList = questionList;
        this.modifiedAt = modifiedAt;
        this.description = description;
    }

    public Survey(JsonObject json) {
        id = JsonHelper.getString(json, "_id");
        name = JsonHelper.getString(json, "name");
        modifiedAt = JsonHelper.getString(json, "modifiedAt");
        description = JsonHelper.getString(json, "description");

        JsonArray questionsArray = JsonHelper.getJsonArray(json, "questions");
        questionList = new ArrayList<>();

        if (questionsArray != null) {
            for (JsonElement e : questionsArray) {
                questionList.add(Question.populateQuestion(e.getAsJsonObject()));
            }
        }
    }

    protected Survey(Parcel in) {
        id = in.readString();
        name = in.readString();
        questionList = in.createTypedArrayList(Question.CREATOR);
        modifiedAt = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeTypedList(questionList);
        dest.writeString(modifiedAt);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Survey> CREATOR = new Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel in) {
            return new Survey(in);
        }

        @Override
        public Survey[] newArray(int size) {
            return new Survey[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Question> getQuestionList() {
        return questionList;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public String getDescription() {
        return description;
    }
}
