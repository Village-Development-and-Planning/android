package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.Serializable;
import java.lang.reflect.Type;

public class Text extends BaseObject {
    private final String english;
    private final String tamil;

    public Text(String english, String tamil) {
        this.english = english;
        this.tamil = tamil;
    }

    public String getEnglish() {
        return english;
    }

    public String getTamil() {
        return tamil;
    }
}