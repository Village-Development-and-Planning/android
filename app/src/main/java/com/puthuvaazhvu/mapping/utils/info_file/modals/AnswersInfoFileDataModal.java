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

/*
    Info JSON structure :

    {
        "surveys": [
            {
                "_id": <string>,
                "survey_name": <string>,
                "survey_uuid": <string>,
                "is_incomplete": true/false
                "timestamp": <string>
            }
        ]
    }

 */

public class AnswersInfoFileDataModal {
    private List<AnswerDataModal> surveys;

    private AnswersInfoFileDataModal(AnswerDataModal data) {
        this.surveys = new ArrayList<>();
        this.surveys.add(data);
    }

    public AnswersInfoFileDataModal(JsonObject jsonObject) {
        surveys = getSurveyDataInternal(jsonObject);
    }

    public void updateWithNew(AnswersInfoFileDataModal other) {
        if (other.surveys == null) {
            throw new IllegalArgumentException("DataModal cannot be null");
        }

        if (isDataEmpty()) {
            this.surveys = new ArrayList<>();
        }

        this.surveys.addAll(other.surveys);
    }

    public List<AnswerDataModal> getSurveys() {
        return surveys;
    }

    public boolean isDataEmpty() {
        return this.surveys == null;
    }

    public JsonObject getAsJson() {
        JsonObject jsonObject = new JsonObject();

        JsonArray surveysArray = new JsonArray();
        for (DataModal data : this.surveys) {
            surveysArray.add(data.getAsJson());
        }

        jsonObject.add("surveys", surveysArray);

        return jsonObject;
    }

    private static List<AnswerDataModal> getSurveyDataInternal(JsonObject jsonObject) {
        JsonArray array = JsonHelper.getJsonArray(jsonObject, "surveys");

        if (array != null) {
            ArrayList<AnswerDataModal> surveyInfoFileDataList = new ArrayList<>();
            for (JsonElement e : array) {
                surveyInfoFileDataList.add(new AnswerDataModal(e.getAsJsonObject()));
            }
            return surveyInfoFileDataList;
        } else {
            return null;
        }
    }

    public static AnswersInfoFileDataModal adapter(String id, String name, String uuid, boolean isIncomplete, String timeStamp) {
        return new AnswersInfoFileDataModal(new AnswerDataModal(id, name, timeStamp, uuid, isIncomplete));
    }
}
