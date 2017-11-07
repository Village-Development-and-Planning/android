package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;
import com.puthuvaazhvu.mapping.views.activities.save_survey_data.SurveyInfoData;
import com.puthuvaazhvu.mapping.views.activities.survey_list.SurveyListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */


/*
    Info JSON structure :

    {
        "surveys": [
            {
                "_id": 1234,
                "survey_name": abcd,
                "timestamp": xxx
            }
        ]
    }

 */

public class SavedSurveyInfoFileData {
    private List<Data> data;

    private SavedSurveyInfoFileData(List<Data> data) {
        this.data = data;
    }

    public SavedSurveyInfoFileData(JsonObject jsonObject) {
        data = getSurveyDataInternal(jsonObject);
    }

    public void updateWithNew(SavedSurveyInfoFileData other) {
        if (other.data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        if (isDataEmpty()) {
            this.data = new ArrayList<>();
        }

        this.data.addAll(other.data);
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getSurveyData() {
        return data;
    }

    public boolean isDataEmpty() {
        return this.data == null;
    }

    public JsonObject getAsJson() {
        JsonObject jsonObject = new JsonObject();

        JsonArray surveysArray = new JsonArray();
        for (Data data : this.data) {
            surveysArray.add(data.getAsJson());
        }

        jsonObject.add("surveys", surveysArray);

        return jsonObject;
    }

    private static List<Data> getSurveyDataInternal(JsonObject jsonObject) {
        JsonArray array = JsonHelper.getJsonArray(jsonObject, "surveys");

        if (array != null) {
            ArrayList<Data> surveyInfoFileDataList = new ArrayList<>();
            for (JsonElement e : array) {
                surveyInfoFileDataList.add(new Data(e.getAsJsonObject()));
            }
            return surveyInfoFileDataList;
        } else {
            return null;
        }
    }

    public static SavedSurveyInfoFileData adapter(List<SurveyInfoData> data) {
        ArrayList<Data> dataList = new ArrayList<>();

        for (SurveyInfoData d : data) {
            dataList.add(Data.adapter(d));
        }

        return new SavedSurveyInfoFileData(dataList);
    }
}
