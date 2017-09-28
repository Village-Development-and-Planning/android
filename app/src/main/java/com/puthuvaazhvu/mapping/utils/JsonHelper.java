package com.puthuvaazhvu.mapping.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class JsonHelper {
    public static boolean isJsonValid(JsonObject jsonObject, String key) {
        return !jsonObject.isJsonNull() && jsonObject.has(key) && !jsonObject.get(key).isJsonNull();
    }

    public static String getString(JsonObject jsonObject, String key) {
        if (isJsonValid(jsonObject, key)) {
            return jsonObject.get(key).getAsString();
        }
        return "";
    }

    public static JsonObject getJsonObject(JsonObject jsonObject, String key) {
        if (isJsonValid(jsonObject, key)) {
            return jsonObject.get(key).getAsJsonObject();
        }
        return null;
    }

    public static ArrayList<String> getStringArray(JsonArray stringArray) {
        ArrayList<String> result = new ArrayList<>();
        for (JsonElement e : stringArray) {
            result.add(e.getAsString());
        }
        return result;
    }

    public static JsonArray getJsonArray(JsonObject jsonObject, String key) {
        if (isJsonValid(jsonObject, key)) {
            return jsonObject.get(key).getAsJsonArray();
        }
        return null;
    }
}
