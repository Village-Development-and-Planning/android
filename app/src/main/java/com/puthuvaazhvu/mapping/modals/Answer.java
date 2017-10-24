package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/26/17.
 */

public class Answer extends BaseObject implements Parcelable {
    private final ArrayList<Option> options;
    private final ArrayList<Question> children;
    private final Question questionReference;

    public Answer(ArrayList<Option> options, ArrayList<Question> children, Question questionReference) {
        this.options = options;
        this.children = children;
        this.questionReference = questionReference;

        if (children != null) {
            for (Question ac : children) {
                ac.replaceParent(questionReference); // set the children parent as the current parent for back reference.
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public ArrayList<String> getOptionListPositions() {
        ArrayList<String> positions = new ArrayList<>();
        for (Option o : options) {
            positions.add(o.getPosition());
        }
        return positions;
    }

    public ArrayList<Question> getChildren() {
        return children;
    }

    public Question getQuestionReference() {
        return questionReference;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.options);
        dest.writeTypedList(this.children);
        dest.writeParcelable(this.questionReference, flags);
    }

    protected Answer(Parcel in) {
        this.options = in.createTypedArrayList(Option.CREATOR);
        this.children = in.createTypedArrayList(Question.CREATOR);
        this.questionReference = in.readParcelable(Question.class.getClassLoader());
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel source) {
            return new Answer(source);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    @Override
    public JsonElement getAsJson() {
        JsonObject jsonObject = new JsonObject();

        JsonArray loggedOptionsArray = new JsonArray();

        for (Option option : options) {
            loggedOptionsArray.add(option.getAsJson());
        }

        JsonArray childrenArray = new JsonArray();

        for (Question c : children) {
            childrenArray.add(c.getAsJson());
        }

        jsonObject.add("logged_options", loggedOptionsArray);
        jsonObject.add("children", childrenArray);

        return jsonObject;
    }

    @Override
    public String toString() {
        String string = "";

        string += "Options count: " + options.size();
        string += " Children [";

        for (Question c : children) {
            string += c.getRawNumber();
            string += ", ";
        }

        string += "]";

        string += " Reference question : " + questionReference.getRawNumber();

        return string;
    }
}
