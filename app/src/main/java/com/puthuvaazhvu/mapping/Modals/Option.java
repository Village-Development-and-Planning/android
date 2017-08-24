package com.puthuvaazhvu.mapping.Modals;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Option implements Parcelable {
    String id;
    String type;
    Text text;
    String modifiedAt;
    String position;

    public Option(String id, String type, Text text, String modifiedAt, String position) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.modifiedAt = modifiedAt;
        this.position = position;
    }

    protected Option(Parcel in) {
        id = in.readString();
        type = in.readString();
        text = in.readParcelable(Text.class.getClassLoader());
        modifiedAt = in.readString();
        position = in.readString();
    }

    public static final Creator<Option> CREATOR = new Creator<Option>() {
        @Override
        public Option createFromParcel(Parcel in) {
            return new Option(in);
        }

        @Override
        public Option[] newArray(int size) {
            return new Option[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Text getText() {
        return text;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(type);
        parcel.writeParcelable(text, i);
        parcel.writeString(modifiedAt);
        parcel.writeString(position);
    }
}
