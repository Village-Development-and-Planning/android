package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

/**
 * Created by muthuveerappans on 11/11/17.
 */

public class AnswerDataModal extends DataModal {
    private final String survey_uuid;
    private final boolean is_incomplete;

    public AnswerDataModal(String _id, String survey_name, String timestamp, String surveyUUID, boolean isIncomplete) {
        super(_id, survey_name, timestamp);
        this.survey_uuid = surveyUUID;
        this.is_incomplete = isIncomplete;
    }

    public AnswerDataModal(JsonObject jsonObject) {
        super(jsonObject);

        survey_uuid = JsonHelper.getString(jsonObject, "survey_uuid");
        is_incomplete = JsonHelper.getBoolean(jsonObject, "is_incomplete");
    }

    public String getSurvey_uuid() {
        return survey_uuid;
    }

    public boolean is_incomplete() {
        return is_incomplete;
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject jsonObject = super.getAsJson();

        jsonObject.addProperty("survey_uuid", survey_uuid);
        jsonObject.addProperty("is_incomplete", is_incomplete);

        return jsonObject;
    }
}
