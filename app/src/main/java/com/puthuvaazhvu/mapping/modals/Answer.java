package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/26/17.
 */

public class Answer extends BaseObject implements Parcelable {
    private final ArrayList<Option> options;
    private final ArrayList<Question> children;
    private Question questionReference;
    private long timeStamp;
    //private int nextVisibleChildIndex;

    public Answer(ArrayList<Option> options, Question questionReference, long timeStamp) {
        this(options, questionReference);
        this.timeStamp = timeStamp;
    }

    public Answer(ArrayList<Option> options, Question questionReference) {
        this.options = options;
        this.questionReference = questionReference;

        this.children = new ArrayList<>();

        if (questionReference != null) {
            Question questionReferenceCopy = questionReference.copy();
            this.children.addAll(questionReferenceCopy.getChildren());
        }

        for (int i = 0; i < children.size(); i++) {
            children.get(i).setParentAnswer(this);
        }
    }

    public void setQuestionReference(Question questionReference) {
        this.questionReference = questionReference;
    }

//    public void setNextVisibleChildIndex(int currentChildIndex) {
//        this.nextVisibleChildIndex = currentChildIndex;
//    }
//
//    public boolean isVisibleChildIndexOutOfBounds(int index) {
//        return index < 0 || index >= children.size();
//    }
//
//    public void nextVisibleChildIndex() {
//        nextVisibleChildIndex++;
//    }
//
//    public void decrementChildIndex() {
//        nextVisibleChildIndex--;
//    }
//
//
//    public int getNextVisibleChildIndex() {
//        return nextVisibleChildIndex;
//    }

//    public static boolean isAnswerDummy(Answer answer) {
//        return answer.getOptions().size() > 0 && answer.getOptions().get(0).getId().equals("DUMMY");
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Option> otherOptions) {
        options.clear();
        options.addAll(otherOptions);
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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
        //dest.writeParcelable(this.questionReference, flags);
    }

    protected Answer(Parcel in) {
        this.options = in.createTypedArrayList(Option.CREATOR);
        this.children = in.createTypedArrayList(Question.CREATOR);
        //this.questionReference = in.readParcelable(Question.class.getClassLoader());
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

        if (options != null) {
            for (Option option : options) {
                loggedOptionsArray.add(option.getAsJson());
            }
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

        string += "hashcode : " + Integer.toHexString(System.identityHashCode(this));

        string += "\nOptions count: " + (options == null ? 0 : options.size());
        string += "\nChildren [";

        for (Question c : children) {
            string += c.getRawNumber();
            string += ", ";
        }

        string += "]";

        string += "\nReference question :" + questionReference.getRawNumber();

        return string;
    }

    @Override
    public Answer copy() {
        return new Answer(
                options,
                questionReference
        );
    }
}
