package com.puthuvaazhvu.mapping.Modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/26/17.
 */

public class Tag implements Parcelable {
    private String tag;

    public Tag(String tag) {
        this.tag = tag;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tag);
    }

    protected Tag(Parcel in) {
        this.tag = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public static ArrayList<Tag> getTags(JsonArray tagsJsonArray) {
        ArrayList<String> tagsString = JsonHelper.getStringArray(tagsJsonArray);
        ArrayList<Tag> tags = new ArrayList<>();
        for (String t : tagsString) {
            tags.add(new Tag(t));
        }
        return tags;
    }
}
