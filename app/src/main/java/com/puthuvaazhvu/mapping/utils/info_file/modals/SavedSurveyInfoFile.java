package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SavedSurveyInfoFile {
    private final String _id;

    public SavedSurveyInfoFile(String _id) {
        this._id = _id;
    }

    public SavedSurveyInfoFile(JsonObject jsonObject) {
        _id = JsonHelper.getString(jsonObject, "_id");
    }

    public String get_id() {
        return _id;
    }
}
