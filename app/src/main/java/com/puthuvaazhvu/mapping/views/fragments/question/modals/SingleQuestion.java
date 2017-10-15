package com.puthuvaazhvu.mapping.views.fragments.question.modals;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public class SingleQuestion implements Parcelable {
    private String id;
    private String text;
    private String rawNumber;
    private String position;

    public SingleQuestion(String id, String text, String rawNumber, String position) {
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

    protected SingleQuestion(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.rawNumber = in.readString();
        this.position = in.readString();
    }

    public static final Creator<SingleQuestion> CREATOR = new Creator<SingleQuestion>() {
        @Override
        public SingleQuestion createFromParcel(Parcel source) {
            return new SingleQuestion(source);
        }

        @Override
        public SingleQuestion[] newArray(int size) {
            return new SingleQuestion[size];
        }
    };

    public static SingleQuestion adapter(com.puthuvaazhvu.mapping.modals.Question question) {
        SingleQuestion q = new SingleQuestion(question.getId(),
                question.getTextString(),
                question.getRawNumber(),
                question.getPosition());
        return q;
    }
}
