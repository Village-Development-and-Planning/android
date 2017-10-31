package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/31/17.
 */

public class InfoFileData {
    private final JsonObject jsonObject;

    public InfoFileData(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public List<SurveyInfoFileData> getSurveyData(String key) {
        JsonArray array = JsonHelper.getJsonArray(jsonObject, key);

        if (array != null) {
            ArrayList<SurveyInfoFileData> surveyInfoFileDataList = new ArrayList<>();
            for (JsonElement e : array) {
                surveyInfoFileDataList.add(new SurveyInfoFileData(e.getAsJsonObject()));
            }
            return surveyInfoFileDataList;
        } else {
            return null;
        }
    }
}
