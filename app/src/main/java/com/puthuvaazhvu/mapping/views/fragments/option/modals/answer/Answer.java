package com.puthuvaazhvu.mapping.views.fragments.option.modals.answer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public abstract class Answer implements Parcelable {
    private final String questionID;
    private final String questionText;

    public Answer(String questionID, String questionText) {
        this.questionID = questionID;
        this.questionText = questionText;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getQuestionText() {
        return questionText;
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
