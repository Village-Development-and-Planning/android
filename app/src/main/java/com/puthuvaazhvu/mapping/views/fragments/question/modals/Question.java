package com.puthuvaazhvu.mapping.views.fragments.question.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.views.fragments.option.modals.*;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Data;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public class Question implements Parcelable {
    private String id;
    private String text;
    private String rawNumber;
    private String position;

    public Question(String id, String text, String rawNumber, String position) {
        this.id = id;
        this.text = text;
        this.rawNumber = rawNumber;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getRawNumber() {
        return rawNumber;
    }

    public String getPosition() {
        return position;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.text);
        dest.writeString(this.rawNumber);
        dest.writeString(this.position);
    }

    protected Question(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.rawNumber = in.readString();
        this.position = in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
