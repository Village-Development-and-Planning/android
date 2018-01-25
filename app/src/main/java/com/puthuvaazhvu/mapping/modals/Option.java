package com.puthuvaazhvu.mapping.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Option extends BaseObject implements Parcelable {
    private String id;
    private final String type;
    private final Text text;
    private final String modifiedAt;
    private final String position;
    private String imageData;

    public Option(String id, String type, Text text, String modifiedAt, String position) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.modifiedAt = modifiedAt;
        this.position = position;
    }

    public Option(String id, String type, Text text, String modifiedAt, String position, String imageData) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.modifiedAt = modifiedAt;
        this.position = position;
        this.imageData = imageData;
    }

    public Option(JsonObject json) {
        position = JsonHelper.getString(json, "position");

        JsonObject optionJson = JsonHelper.getJsonObject(json, "option");
        if (optionJson == null) {
            optionJson = json;
        }
        id = JsonHelper.getString(optionJson, "_id");
        if (id == null) id = "";
        type = JsonHelper.getString(optionJson, "type");
        modifiedAt = JsonHelper.getString(optionJson, "modifiedAt");

        JsonObject textJson = JsonHelper.getJsonObject(optionJson, "text");

        JsonObject imageDataJson = JsonHelper.getJsonObject(optionJson, "image");
        if (imageDataJson != null) {
            imageData = JsonHelper.getString(imageDataJson, "data");
        }

        text = new Text(textJson);
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

    public String getTextString() {
        switch (Constants.APP_LANGUAGE) {
            case TAMIL:
                return text.getTamil();
            default:
                return text.getEnglish();
        }
    }

    public String getImageData() {
        return imageData;
    }

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
        parcel.writeString(imageData);
    }

    public static ArrayList<Option> getOptions(JsonArray optionsJsonArray) {
        ArrayList<Option> optionList = new ArrayList<>();
        for (JsonElement e : optionsJsonArray) {
            optionList.add(new Option(e.getAsJsonObject()));
        }
        return optionList;
    }

    @Override
    public JsonElement getAsJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", id);
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("position", position);
        jsonObject.addProperty("modifiedAt", modifiedAt);

        if (text != null)
            jsonObject.add("text", text.getAsJson());

        return jsonObject;
    }

    @Override
    public Option copy() {
        return new Option(id, type, text.copy(), modifiedAt, position, imageData);
    }
}
