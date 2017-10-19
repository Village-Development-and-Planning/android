package com.puthuvaazhvu.mapping.modals.flow;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.puthuvaazhvu.mapping.modals.BaseObject;

import java.io.Serializable;

public class PostFlow extends BaseObject implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    @Override
    public JsonElement getAsJson() {
        return null;
    }
}
