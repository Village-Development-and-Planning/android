package com.puthuvaazhvu.mapping.Modals;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muthuveerappans on 9/26/17.
 */

public class Answer implements Parcelable {
    private Option option;
    private Question question;

    public Answer(Option option, Question question) {
        this.option = option;
        this.question = question;
    }

    public Option getOption() {
        return option;
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
        dest.writeParcelable(this.option, flags);
        dest.writeParcelable(this.question, flags);
    }

    protected Answer(Parcel in) {
        this.option = in.readParcelable(Option.class.getClassLoader());
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
