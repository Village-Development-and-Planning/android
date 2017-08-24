package com.puthuvaazhvu.mapping.Modals;

import android.os.Parcel;
import android.os.Parcelable;

public class Text implements Parcelable {
    String id;
    String english;
    String tamil;

    public Text(String id, String english, String tamil) {
        this.id = id;
        this.english = english;
        this.tamil = tamil;
    }

    protected Text(Parcel in) {
        id = in.readString();
        english = in.readString();
        tamil = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(english);
        dest.writeString(tamil);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Text> CREATOR = new Creator<Text>() {
        @Override
        public Text createFromParcel(Parcel in) {
            return new Text(in);
        }

        @Override
        public Text[] newArray(int size) {
            return new Text[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getEnglish() {
        return english;
    }

    public String getTamil() {
        return tamil;
    }
}