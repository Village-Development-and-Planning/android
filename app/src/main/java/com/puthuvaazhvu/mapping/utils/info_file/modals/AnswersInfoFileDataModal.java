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
        "answerDataModals": [
            {
                _id: <string>,
                is_over: true/false,
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
    private List<AnswerDataModal> answerDataModals;
    private final int version;

    public AnswersInfoFileDataModal(List<AnswerDataModal> answerDataModals, int version) {
        this.answerDataModals = answerDataModals;
        this.version = version;
    }

    private AnswersInfoFileDataModal(AnswerDataModal data, int version) {
        this.answerDataModals = new ArrayList<>();
        this.answerDataModals.add(data);

        this.version = version;
    }

    public AnswersInfoFileDataModal(JsonObject jsonObject) {
        version = JsonHelper.getInt(jsonObject, "version");

        JsonArray array = JsonHelper.getJsonArray(jsonObject, "surveys");

        answerDataModals = new ArrayList<>();

        if (array != null) {
            for (JsonElement e : array) {
                answerDataModals.add(new AnswerDataModal(e.getAsJsonObject()));
            }
        }
    }

    public AnswerDataModal find(String surveyID) {
        for (AnswerDataModal answerDataModal : answerDataModals) {
            if (answerDataModal.getId().equals(surveyID)) {
                return answerDataModal;
            }
        }
        return null;
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

        List<AnswerDataModal> thisAnswerDataModals = this.answerDataModals;

        for (AnswerDataModal otherAnswerDataModal : otherSurveys) {

            AnswerDataModal thisAnswerDataModal = findAnswerDataModal(thisAnswerDataModals, otherAnswerDataModal);

            if (thisAnswerDataModal == null) {
                thisAnswerDataModals.add(otherAnswerDataModal);
            } else {
                thisAnswerDataModal.updateOther(otherAnswerDataModal);
            }
        }
    }

    public void setAnswerDataModals(List<AnswerDataModal> answerDataModals) {
        this.answerDataModals = answerDataModals;
    }

    public List<AnswerDataModal> getAnswerDataModals() {
        return answerDataModals;
    }

    public int getVersion() {
        return version;
    }

    public boolean isDataEmpty() {
        return this.answerDataModals == null || this.answerDataModals.size() <= 0;
    }

    public JsonObject getAsJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("version", version);

        JsonArray surveysArray = new JsonArray();
        for (AnswerDataModal data : this.answerDataModals) {
            surveysArray.add(data.getAsJson());
        }

        jsonObject.add("answerDataModals", surveysArray);

        return jsonObject;
    }

    private static AnswerDataModal findAnswerDataModal(List<AnswerDataModal> surveys, AnswerDataModal toFind) {
        for (AnswerDataModal survey : surveys) {
            if (survey.getId().equals(toFind.getId())) {
                return survey;
            }
        }
        return null;
    }

    private static List<AnswerDataModal> getSurveyDataInternal(JsonObject jsonObject) {
        JsonArray array = JsonHelper.getJsonArray(jsonObject, "answerDataModals");

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
