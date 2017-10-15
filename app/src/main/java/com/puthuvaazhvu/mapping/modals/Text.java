package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.Serializable;

public class Text implements Parcelable, Serializable {
    private final String id;
    private final String english;
    private final String tamil;
    private final String modifiedAt;

    public Text(String id, String english, String tamil, String modifiedAt) {
        this.id = id;
        this.english = english;
        this.tamil = tamil;
        this.modifiedAt = modifiedAt;
    }

    public Text(JsonObject jsonObject) {
        english = JsonHelper.getString(jsonObject, "english");
        tamil = JsonHelper.getString(jsonObject, "tamil");
        id = JsonHelper.getString(jsonObject, "_id");
        modifiedAt = JsonHelper.getString(jsonObject, "modifiedAt");
    }

    protected Text(Parcel in) {
        id = in.readString();
        english = in.readString();
        tamil = in.readString();
        modifiedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(english);
        dest.writeString(tamil);
        dest.writeString(modifiedAt);
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

    public String getModifiedAt() {
        return modifiedAt;
    }
}