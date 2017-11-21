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
        "version": <int>,
        "surveys": [
            {
                _id: <string>
                snap_shots: [
                    {
                        "snapshot_id": <string>,
                        "survey_name": <string>,
                        "path_to_last_question": <string>,
                        "is_incomplete": true/false
                        "timestamp": <string>
                    }
                ]
            }
        ]
    }

 */

public class AnswersInfoFileDataModal {
    private List<AnswerDataModal> surveys;
    private final int version;

    public AnswersInfoFileDataModal(List<AnswerDataModal> surveys, int version) {
        this.surveys = surveys;
        this.version = version;
    }

    private AnswersInfoFileDataModal(AnswerDataModal data, int version) {
        this.surveys = new ArrayList<>();
        this.surveys.add(data);

        this.version = version;
    }

    public AnswersInfoFileDataModal(JsonObject jsonObject) {
        version = JsonHelper.getInt(jsonObject, "version");

        JsonArray array = JsonHelper.getJsonArray(jsonObject, "surveys");

        surveys = new ArrayList<>();

        if (array != null) {
            for (JsonElement e : array) {
                surveys.add(new AnswerDataModal(e.getAsJsonObject()));
            }
        }
    }

    public void updateWithNew(ArrayList<AnswerDataModal> otherSurveys) {
        if (otherSurveys == null) {
            throw new IllegalArgumentException("DataModal cannot be null");
        }

        if (isDataEmpty()) {
            throw new IllegalArgumentException("Surveys list cannot be empty");
        }

        // if a survey with the ID exists, save the snapshot under that survey
        // else create a new survey and a snapshot.

        List<AnswerDataModal> thisSurveys = this.surveys;

        for (AnswerDataModal otherSurvey : otherSurveys) {

            AnswerDataModal thisSurvey = findSurvey(thisSurveys, otherSurvey);

            if (thisSurvey == null) {
                thisSurveys.add(otherSurvey);
            } else {
                // empty all the existing snapshots
                thisSurvey.getSnapshots().clear();
                // add the new list
                thisSurvey.getSnapshots().addAll(otherSurvey.getSnapshots());
            }
        }
    }

    public void setSurveys(List<AnswerDataModal> surveys) {
        this.surveys = surveys;
    }

    public List<AnswerDataModal> getSurveys() {
        return surveys;
    }

    public int getVersion() {
        return version;
    }

    public boolean isDataEmpty() {
        return this.surveys == null || this.surveys.size() <= 0;
    }

    public JsonObject getAsJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("version", version);

        JsonArray surveysArray = new JsonArray();
        for (AnswerDataModal data : this.surveys) {
            surveysArray.add(data.getAsJson());
        }

        jsonObject.add("surveys", surveysArray);

        return jsonObject;
    }

    private static AnswerDataModal findSurvey(List<AnswerDataModal> surveys, AnswerDataModal toFind) {
        for (AnswerDataModal survey : surveys) {
            if (survey.getId().equals(toFind.getId())) {
                return survey;
            }
        }
        return null;
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
}
