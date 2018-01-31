package com.puthuvaazhvu.mapping.modals.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Question;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class AuthJsonUtils {
    public static JsonObject getAuthForSurveyCode(JsonObject auth, String surveyCode) {
        JsonElement jsonElement = auth.get(surveyCode);
        if (jsonElement != null) return jsonElement.getAsJsonObject();
        return null;
    }
}
