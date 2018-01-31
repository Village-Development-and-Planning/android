package com.puthuvaazhvu.mapping.modals.flow;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.puthuvaazhvu.mapping.modals.BaseObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.Serializable;
import java.util.ArrayList;

public class PostFlow extends BaseObject implements Parcelable {
    ArrayList<String> tags = new ArrayList<>();

    public PostFlow(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            JsonArray postFlowJsonArray = jsonElement.getAsJsonArray();
            tags = (JsonHelper.getStringArray(postFlowJsonArray));
        }
    }

    protected PostFlow(Parcel in) {
        this.tags = in.createStringArrayList();
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeStringList(this.tags);
    }

    public static final Creator<PostFlow> CREATOR = new Creator<PostFlow>() {
        @Override
        public PostFlow createFromParcel(Parcel source) {
            return new PostFlow(source);
        }

        @Override
        public PostFlow[] newArray(int size) {
            return new PostFlow[size];
        }
    };

    @Override
    public JsonElement getAsJson() {
        return null;
    }
}
