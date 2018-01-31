package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;
import com.puthuvaazhvu.mapping.views.activities.save_survey_data.SurveyInfoData;

@Deprecated
public class DataModal {
    private final String _id;
    private final String survey_name;
    private final String timestamp;

    public DataModal(String _id, String survey_name, String timestamp) {
        this._id = _id;
        this.survey_name = survey_name;
        this.timestamp = timestamp;
    }

    public DataModal(JsonObject jsonObject) {
        _id = JsonHelper.getString(jsonObject, "_id");
        survey_name = JsonHelper.getString(jsonObject, "survey_name");
        timestamp = JsonHelper.getString(jsonObject, "timestamp");
    }

    public String getId() {
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

    public static DataModal adapter(SurveyInfoData data) {
        return new DataModal(data.id, data.name, "" + System.currentTimeMillis());
    }
}