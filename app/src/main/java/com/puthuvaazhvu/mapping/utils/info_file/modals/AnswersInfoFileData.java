package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;
import com.puthuvaazhvu.mapping.views.activities.save_survey_data.SurveyInfoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/31/17.
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

public class AnswersInfoFileData {
    private List<Data> surveys;

    private AnswersInfoFileData(Data data) {
        this.surveys = new ArrayList<>();
        this.surveys.add(data);
    }

    public AnswersInfoFileData(JsonObject jsonObject) {
        surveys = getSurveyDataInternal(jsonObject);
    }

    public void updateWithNew(AnswersInfoFileData other) {
        if (other.surveys == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        if (isDataEmpty()) {
            this.surveys = new ArrayList<>();
        }

        this.surveys.addAll(other.surveys);
    }

    public List<Data> getSurveys() {
        return surveys;
    }

    public boolean isDataEmpty() {
        return this.surveys == null;
    }

    public JsonObject getAsJson() {
        JsonObject jsonObject = new JsonObject();

        JsonArray surveysArray = new JsonArray();
        for (Data data : this.surveys) {
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

    public static AnswersInfoFileData adapter(String id, String name) {
        return new AnswersInfoFileData(new Data(id, name, "" + System.currentTimeMillis()));
    }
}
