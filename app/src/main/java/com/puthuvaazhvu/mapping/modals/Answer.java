package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/26/17.
 */

public class Answer implements Parcelable {
    private ArrayList<Option> options;
    private Question question;

    public Answer(ArrayList<Option> options, Question question) {
        this.options = options;
        this.question = question;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public Question getQuestion() {
        return question;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.options);
        dest.writeParcelable(this.question, flags);
    }

    protected Answer(Parcel in) {
        this.options = in.createTypedArrayList(Option.CREATOR);
        this.question = in.readParcelable(Question.class.getClassLoader());
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
}
