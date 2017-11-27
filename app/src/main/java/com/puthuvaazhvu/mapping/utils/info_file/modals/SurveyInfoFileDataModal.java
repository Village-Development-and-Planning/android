package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;
import com.puthuvaazhvu.mapping.views.activities.save_survey_data.SurveyInfoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */


/*
    Info JSON structure :

    {
        "version": <int>
        "surveys": [
            {
                "_id": 1234,
                "survey_name": abcd,
                "timestamp": xxx
            }
        ]
    }

 */

public class SurveyInfoFileDataModal {
    private final int version;
    private List<DataModal> data;

    public SurveyInfoFileDataModal(int version, List<DataModal> data) {
        this.data = data;
        this.version = version;
    }

    public SurveyInfoFileDataModal(JsonObject jsonObject) {
        version = JsonHelper.getInt(jsonObject, "version");
        data = getSurveyDataInternal(jsonObject);
    }

    public void updateWithNew(List<DataModal> data) {
        if (data == null) {
            throw new IllegalArgumentException("DataModal cannot be null");
        }

        if (isDataEmpty()) {
            this.data = new ArrayList<>();
        }

        this.data.addAll(data);
    }

    public int getVersion() {
        return version;
    }

    public void setData(List<DataModal> data) {
        this.data = data;
    }

    public List<DataModal> getSurveyData() {
        return data;
    }

    public boolean isDataEmpty() {
        return this.data == null;
    }

    public JsonObject getAsJson() {
        JsonObject jsonObject = new JsonObject();

        JsonArray surveysArray = new JsonArray();
        for (DataModal data : this.data) {
            surveysArray.add(data.getAsJson());
        }

        jsonObject.addProperty("version", version);
        jsonObject.add("surveys", surveysArray);

        return jsonObject;
    }

    private static List<DataModal> getSurveyDataInternal(JsonObject jsonObject) {
        JsonArray array = JsonHelper.getJsonArray(jsonObject, "surveys");

        if (array != null) {
            ArrayList<DataModal> surveyInfoFileDataList = new ArrayList<>();
            for (JsonElement e : array) {
                surveyInfoFileDataList.add(new DataModal(e.getAsJsonObject()));
            }
            return surveyInfoFileDataList;
        } else {
            return null;
        }
    }
}