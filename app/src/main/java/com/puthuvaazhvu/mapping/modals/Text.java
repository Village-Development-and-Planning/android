package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;

public class Text extends BaseObject {
    private String english;
    private String tamil;

    public Text() {
    }

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

//    private void writeObject(ObjectOutputStream os) throws IOException {
//        os.writeUTF(english);
//        os.writeUTF(tamil);
//    }
//
//    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
//        english = is.readUTF();
//        tamil = is.readUTF();
//    }
}