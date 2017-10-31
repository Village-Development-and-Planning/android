package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SavedSurveyInfoFileData extends InfoFileData {
    private final List<SurveyInfoFileData> surveyInfoFileDataList;

    public SavedSurveyInfoFileData(JsonObject jsonObject) {
        super(jsonObject);
        surveyInfoFileDataList = getSurveyData("saved_surveys");
    }

    public List<SurveyInfoFileData> getSurveyInfoFileDataList() {
        return surveyInfoFileDataList;
    }
}
