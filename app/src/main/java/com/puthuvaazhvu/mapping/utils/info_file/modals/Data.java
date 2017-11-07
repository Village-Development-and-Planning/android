package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;
import com.puthuvaazhvu.mapping.views.activities.save_survey_data.SurveyInfoData;

public class Data {
    private final String _id;
    private final String survey_name;
    private final String timestamp;

    public Data(String _id, String survey_name, String timestamp) {
        this._id = _id;
        this.survey_name = survey_name;
        this.timestamp = timestamp;
    }

    public Data(JsonObject jsonObject) {
        _id = JsonHelper.getString(jsonObject, "_id");
        survey_name = JsonHelper.getString(jsonObject, "survey_name");
        timestamp = JsonHelper.getString(jsonObject, "timestamp");
    }

    public String get_id() {
        return _id;
    }

    public String getSurveyName() {
        return survey_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public JsonObject getAsJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("_id", _id);
        jsonObject.addProperty("survey_name", survey_name);
        jsonObject.addProperty("timestamp", timestamp);
        return jsonObject;
    }

    public static Data adapter(SurveyInfoData data) {
        return new Data(data.id, data.name, "" + System.currentTimeMillis());
    }
}